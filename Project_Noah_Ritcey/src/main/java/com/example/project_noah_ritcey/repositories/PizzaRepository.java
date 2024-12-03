package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
}