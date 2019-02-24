package com.glovoapp.backender;

import com.glovoapp.backender.api.OrderVM;
import com.glovoapp.backender.entity.Order;
import com.glovoapp.backender.repository.OrderRepository;
import com.glovoapp.backender.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {
    private final String welcomeMessage;
    private final OrderRepository orderRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository) {
        this.welcomeMessage = welcomeMessage;
        this.orderRepository = orderRepository;
    }

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return welcomeMessage;
    }

    @RequestMapping("/orders")
    @ResponseBody
    List<OrderVM> orders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "orders/{courierId}", method = RequestMethod.GET)
    public @ResponseBody
    Collection<Order> getOrders(@PathVariable(value = "courierId") String courierId) {
        return assignmentService.getOrder(courierId);
    }

    public static void main(String[] args) {
        SpringApplication.run(API.class);
    }
}
