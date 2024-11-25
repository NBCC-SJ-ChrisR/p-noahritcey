package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.Employee;
import com.example.project_noah_ritcey.entities.Pizzatopping;
import com.example.project_noah_ritcey.repositories.EmployeeRepository;
import com.example.project_noah_ritcey.repositories.PizzatoppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToppingService {
    private final PizzatoppingRepository pizzaToppingRepository;
    private final EmployeeRepository employeeRepository;

    public List<Pizzatopping> getAllToppings() {
        return pizzaToppingRepository.findAll();
    }

    public void addTopping(String name, Float price) {
        // Get logged-in user's details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            Employee emp = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Employee not found for username: " + username));

            System.out.println("Logged-in Employee ID: " + emp.getId());


            Pizzatopping topping = new Pizzatopping();
            topping.setName(name);
            topping.setPrice(price);
            topping.setIsActive((byte) 1);
            topping.setEmpAddedBy(emp);

            pizzaToppingRepository.save(topping);
            System.out.println("Topping saved successfully!");
        } else {
            System.out.println("Error: Principal is not an instance of UserDetails");
            throw new RuntimeException("Unable to determine the current user ID");
        }
    }

    public void deactivateTopping(Integer id) {
        Pizzatopping topping = pizzaToppingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topping not found"));
        topping.setIsActive((byte) 0);
        pizzaToppingRepository.save(topping);
    }

    public void editTopping(Integer id, String name, Float price) {
        Pizzatopping pizzatopping = pizzaToppingRepository.findById(id).orElseThrow(() -> new RuntimeException("Topping not found"));
        pizzatopping.setName(name);
        pizzatopping.setPrice(price);
        pizzaToppingRepository.save(pizzatopping);
    }

    public void reactivateTopping(Integer id) {
        Pizzatopping topping = pizzaToppingRepository.findById(id).orElseThrow(() -> new RuntimeException("Topping not found"));
        topping.setIsActive((byte) 1);
        pizzaToppingRepository.save(topping);
    }

    public List<Pizzatopping> getAllActiveToppings() {
        List<Pizzatopping> toppings = pizzaToppingRepository.findByIsActive((byte) 1);
        System.out.println("Active toppings count: " + toppings.size());
        return toppings;
    }
}
