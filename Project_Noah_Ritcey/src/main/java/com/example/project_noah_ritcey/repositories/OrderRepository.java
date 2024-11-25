package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByOrderStatus(String orderStatus);
}