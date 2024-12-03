package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Pizzasize;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PizzasizeRepository extends JpaRepository<Pizzasize, Integer> {
}