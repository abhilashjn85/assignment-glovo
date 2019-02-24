package com.glovoapp.backender.service;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.util.DistanceCalculator;
import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.enums.ClusterDistanceDeciderType;
import com.glovoapp.backender.util.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("haversineHandler")
@Primary
public class HaversineClusterHandler implements ClusterService {

    @Value("${backender.glovo.distance:500}")
    private Double clusterWindow;

    @Override
    public ClusterDistanceDeciderType getSubject() {
        return ClusterDistanceDeciderType.HAVERSINE;
    }

    /**
     * This will group all the orders in chunks of 500, 1000m or so where 500m is configurable. (say X, 2*X, 3*X).
     * 0 -> contains all orders within 0 - 500m (Or, 0 - Xm)
     * 1 -> contain all orders within 500m - 1000m (Or, Xm - 2*Xm)
     *
     * Then sorting by distance for each group.
     *
     * @param courier
     * @param orders
     * @return
     */
    @Override
    public Map<Double, List<Order>> getClustersOrderByDistance(Courier courier, Collection<Order> orders) {

        Map<Double, List<Order>> ordersGroupByDistance = getClusterDistanceBySlots(courier, orders);

        ordersGroupByDistance.forEach((key, value) -> {
            value.sort( (o1, o2) -> DistanceCalculator.calculateDistance(courier.getLocation(), o1.getDelivery())
                    > DistanceCalculator.calculateDistance(courier.getLocation(), o2.getDelivery()) ? 1 : -1);
        });

        return ordersGroupByDistance;
    }

    /**
     * This will group all the orders in chunks of 500, 1000m or so where 500m is configurable. (say X, 2*X, 3*X).
     *
     * @param courier
     * @param orders
     * @return
     */
    private Map<Double, List<Order>> getClusterDistanceBySlots(Courier courier, Collection<Order> orders) {
        return orders
                .stream()
                .collect(Collectors.groupingBy(o -> Math.floor(DistanceCalculator.calculateDistance(courier.getLocation(), o.getDelivery()) * Constant.KM_CONVERSION / clusterWindow),
                        Collectors.toList()));
    }
}
