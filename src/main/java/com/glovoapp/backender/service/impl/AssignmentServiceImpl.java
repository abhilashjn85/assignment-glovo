package com.glovoapp.backender.service.impl;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.entity.Vehicle;
import com.glovoapp.backender.enums.ClusterDistanceDeciderType;
import com.glovoapp.backender.exception.BaseException;
import com.glovoapp.backender.repository.OrderRepository;
import com.glovoapp.backender.service.AssignmentService;
import com.glovoapp.backender.service.ClusterService;
import com.glovoapp.backender.service.CourierService;
import com.glovoapp.backender.util.Constant;
import com.glovoapp.backender.util.DistanceCalculator;
import com.glovoapp.backender.util.ErrorCodes;
import com.glovoapp.backender.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component("assignmentService")
public class AssignmentServiceImpl implements AssignmentService {

    private Map<ClusterDistanceDeciderType, ClusterService> clusterServiceHandlerMap;

    @Value("#{'${backender.glovo.box.description:}'.split(',')}")
    private List<String> orderTypes;


    @Value("#{'${backender.glovo.priority:}'.split(',')}")
    private List<String> priorityOrderTypes;

    @Autowired
    private CourierService courierService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    public void setClusterServiceHandler(Set<ClusterService> clusterHandlers) {
        clusterServiceHandlerMap = new HashMap<>();
        clusterHandlers.forEach(handler -> clusterServiceHandlerMap.put(handler.getSubject(), handler));
    }


    @Override
    @Cacheable(value = "getOrder", key = "#courierId")
    public Collection<Order> getOrder(String courierId) {

        Courier courier = courierService.findByCourierId(courierId);
        Map<Double, List<Order>> reOrderedMap = new HashMap<>();

        // filtering on glovo box and only Motorcycle/ELECTRIC_SCOOTER will see the order which is 5km
        Map<Boolean, List<Order>> glovoOrNoNGlovoBoxOrdersMap = Optional.ofNullable(orderRepository.findAll()).orElse(new ArrayList<>())
                .stream()
                .filter(o -> showOrdersOnVehicleFilter(o, courier))
                .collect(
                        Collectors.partitioningBy((Order o)  -> !Collections.disjoint(
                                Arrays.asList(o.getDescription().split(" ")), orderTypes)));

        // fetch filtered orders based on courier has glovo box or not
        List<Order> filteredOrders = glovoOrNoNGlovoBoxOrdersMap.get(courier.getBox());

        // fetch all orders in a group of 500m or so, And sort by distance in each group.
        Map<Double, List<Order>> orders = Optional.ofNullable(clusterServiceHandlerMap.get(ClusterDistanceDeciderType.HAVERSINE))
                .orElseThrow(() -> new BaseException(ErrorCodes.INTERNAL_CONFIG_ERROR, String.format("No handler found for [%s]", ClusterDistanceDeciderType.HAVERSINE)))
                .getClustersOrderByDistance(courier, filteredOrders);


        // re-ordering on the basis of priority
        reOrderingBasedOnPriority(reOrderedMap, orders);

        return Utils.flatten(reOrderedMap.values())
                .collect(Collectors.toList());
    }

    /**
     * This will reOrder the orders based on the priority given {backender.glovo.priority:}, Simply grouping by and adding to list in that order.
     *
     * @param reOrderedMap
     * @param orders
     */
    private void reOrderingBasedOnPriority(Map<Double, List<Order>> reOrderedMap, Map<Double, List<Order>> orders) {
        orders.forEach((key, value) ->{

            List<Order> newOrderedList = new ArrayList<>();
            Map<String, List<Order>> groupedByPriorityTypes = value.stream()
                    .collect(Collectors.groupingBy(o -> o.getVip() ? Constant.VIP : o.getFood() ? Constant.FOOD : Constant.OTHERS,
                            Collectors.toList()));

            Optional.ofNullable(priorityOrderTypes).orElse(new ArrayList<>()).stream().forEach(priorityOrderType -> {
                List<Order> priorityOrders = groupedByPriorityTypes.get(priorityOrderType);
                if(!CollectionUtils.isEmpty(priorityOrders)) {
                    newOrderedList.addAll(priorityOrders);
                }
            });

            reOrderedMap.put(key, CollectionUtils.isEmpty(newOrderedList) ? value : newOrderedList);
        });
    }

    /**
     * Fulfilling: If the order is further than 5km to the courier, we will only show it to
     * couriers that move in motorcycle or electric scooter.
     *
     * @param o
     * @param courier
     * @return
     */
    private boolean showOrdersOnVehicleFilter(Order o, Courier courier) {

        return ((Vehicle.MOTORCYCLE.equals(courier.getVehicle()) || Vehicle.ELECTRIC_SCOOTER.equals(courier.getVehicle()))
                && DistanceCalculator.calculateDistance(courier.getLocation(), o.getDelivery()) > 5.) ||
                (Arrays.stream(Vehicle.values()).anyMatch(s -> s.equals(courier.getVehicle()) && DistanceCalculator.calculateDistance(courier.getLocation(), o.getDelivery()) < 5.));
    }
}
