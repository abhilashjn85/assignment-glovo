package com.glovoapp.backender.controller;

import com.glovoapp.backender.repository.OrderRepository;
import com.glovoapp.backender.api.OrderVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/")
public class OrderController {

    @Autowired
    public OrderRepository orderRepository;

    @RequestMapping(value = "orders", method = RequestMethod.GET)
    @ResponseBody
    List<OrderVM> orders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }
}
