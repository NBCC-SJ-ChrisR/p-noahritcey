package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.PizzatoppingMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaToppingMapRepository extends JpaRepository<PizzatoppingMap, Integer> {
    List<PizzatoppingMap> findByPizzaId(Integer pizzaId);
}