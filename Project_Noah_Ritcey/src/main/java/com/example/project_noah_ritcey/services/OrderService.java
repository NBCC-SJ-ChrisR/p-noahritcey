package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.Order;
import com.example.project_noah_ritcey.entities.Pizzatopping;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PizzasizeRepository pizzaSizeRepository;
    private final PizzacrustRepository pizzaCrustRepository;
    private final PizzaToppingMapRepository pizzaToppingMapRepository;
    private final CustomerRepository customerRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatus(status);
    }

    public void changeStatusToFilled(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("FULFILLED");
        orderRepository.save(order);
    }
}
