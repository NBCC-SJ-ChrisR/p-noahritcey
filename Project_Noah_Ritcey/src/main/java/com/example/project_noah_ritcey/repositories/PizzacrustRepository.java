package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Pizzacrust;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PizzacrustRepository extends JpaRepository<Pizzacrust, Integer> {
}