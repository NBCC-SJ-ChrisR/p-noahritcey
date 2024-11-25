package com.example.project_noah_ritcey.repositories;

import com.example.project_noah_ritcey.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}