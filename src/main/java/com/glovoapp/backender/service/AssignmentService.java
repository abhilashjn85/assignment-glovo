package com.glovoapp.backender.service;

import com.glovoapp.backender.entity.Order;

import java.util.Collection;

public interface AssignmentService {
    Collection<Order> getOrder(String courierId);
}
