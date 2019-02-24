package com.glovoapp.backender.service;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.enums.ClusterDistanceDeciderType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ClusterService {

    ClusterDistanceDeciderType getSubject();
    Map<Double, List<Order>> getClustersOrderByDistance(Courier courier, Collection<Order> orders);
}
