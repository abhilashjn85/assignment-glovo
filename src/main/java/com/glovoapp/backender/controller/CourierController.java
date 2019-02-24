package com.glovoapp.backender.controller;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.repository.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/")
public class CourierController {

    @Autowired
    public CourierRepository courierRepository;

    @RequestMapping(value = "courier", method = RequestMethod.GET)
    @ResponseBody
    public List<Courier> getCourier() {
        return courierRepository.findAll()
                .stream()
                .map(courier -> new Courier(courier.getId(), courier.getName(), courier.getBox(), courier.getVehicle(), courier.getLocation()))
                .collect(Collectors.toList());
    }
}

