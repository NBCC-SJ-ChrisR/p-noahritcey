package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.PizzaToppingMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaToppingMapRepository extends JpaRepository<PizzaToppingMap, Integer> {
    List<PizzaToppingMap> findByPizzaId(Integer pizzaId);
}