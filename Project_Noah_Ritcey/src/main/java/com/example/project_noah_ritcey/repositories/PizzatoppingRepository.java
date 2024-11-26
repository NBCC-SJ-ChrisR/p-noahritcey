package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Pizzatopping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzatoppingRepository extends JpaRepository<Pizzatopping, Integer> {
  List<Pizzatopping> findByIsActive(Byte isActive);
}