package com.glovoapp.backender.service;

import com.glovoapp.backender.entity.Courier;
import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.enums.ClusterDistanceDeciderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("elasticClusterHandler")
public class ElasticSearchClusterHandler implements ClusterService {

    @Value("${backender.glovo.distance:500}")
    private Double clusterWindow;

    @Override
    public ClusterDistanceDeciderType getSubject() {
        return ClusterDistanceDeciderType.ELASTIC_SEARCH;
    }


    @Override
    public Map<Double, List<Order>> getClustersOrderByDistance(Courier courier, Collection<Order> orders) {

        //TODO: need to use spatial of elastic search.
        return new HashMap<>();
    }
}
