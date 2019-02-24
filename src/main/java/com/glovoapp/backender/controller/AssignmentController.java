package com.glovoapp.backender.controller;

import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @RequestMapping(value = "orders/{courierId}", method = RequestMethod.GET)
    public @ResponseBody
    Collection<Order> getOrders(@PathVariable(value = "courierId") String courierId) {
        return assignmentService.getOrder(courierId);
    }
}
