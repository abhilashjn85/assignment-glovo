package com.glovoapp.backender.service.impl;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.repository.CourierRepository;
import com.glovoapp.backender.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("courierServiceImpl")
public class CourierServiceImpl implements CourierService {

    @Autowired
    private CourierRepository courierRepository;

    @Override
    public List<Courier> find() {
        return courierRepository.findAll()
                .stream()
                .map(courier -> new Courier(courier.getId(), courier.getName(), courier.getBox(), courier.getVehicle(), courier.getLocation()))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "courier", key = "#courierId")
    public Courier findByCourierId(String courierId) {
        return courierRepository.findById(courierId);
    }
}
