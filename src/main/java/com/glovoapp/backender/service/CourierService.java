package com.glovoapp.backender.service;

import com.glovoapp.backender.entity.Courier;

import java.util.List;

public interface CourierService {
    List<Courier> find();
    Courier findByCourierId(String courierId);
}
