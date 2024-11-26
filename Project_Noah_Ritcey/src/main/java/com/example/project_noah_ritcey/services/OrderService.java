package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PizzasizeRepository pizzaSizeRepository;
    private final PizzacrustRepository pizzaCrustRepository;
    private final PizzaToppingMapRepository pizzaToppingMapRepository;
    private final CustomerRepository customerRepository;
    private final PizzaService pizzaService;
    private final PizzaRepository pizzaRepository;

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

    public Order createOrder(Customer customer, List<Pizza> cartPizzas, LocalDateTime deliveryTime) {
        // Calculate totals
        Float subtotal = pizzaService.calculateSubTotal(cartPizzas);
        Float tax = pizzaService.calculateTax(subtotal);
        Float total = pizzaService.calculateTotalPrice(subtotal);

        // Create and save order
        Order order = new Order();
        order.setCustomer(customer);
        order.setSubTotal(subtotal);
        order.setHst(tax);
        order.setOrderTotal(total);
        order.setOrderStatus("PENDING");
        order.setDeliveryDate(deliveryTime.toInstant(java.time.ZoneOffset.UTC));
        order.setOrderPlacedDate(Instant.now());
        order.setPizzas(new LinkedHashSet<>());

        // Save order first
        Order savedOrder = orderRepository.save(order);

        // Process each pizza
        for (Pizza cartPizza : cartPizzas) {
            Pizza orderPizza = new Pizza();
            orderPizza.setOrder(savedOrder);
            orderPizza.setPizzaSize(cartPizza.getPizzaSize());
            orderPizza.setPizzaCrust(cartPizza.getPizzaCrust());
            orderPizza.setQuantity(cartPizza.getQuantity());
            orderPizza.setPriceEach(cartPizza.getPriceEach());
            orderPizza.setTotalPrice(cartPizza.getTotalPrice());
            orderPizza.setPizzatoppingMaps(new LinkedHashSet<>());

            // Save pizza
            Pizza savedPizza = pizzaRepository.save(orderPizza);

            // Add pizza to order's set
            savedOrder.getPizzas().add(savedPizza);

            // Process toppings
            for (PizzaToppingMap cartMapping : cartPizza.getPizzatoppingMaps()) {
                PizzaToppingMap newMapping = new PizzaToppingMap();
                newMapping.setPizza(savedPizza);
                newMapping.setPizzaTopping(cartMapping.getPizzaTopping());
                pizzaToppingMapRepository.save(newMapping);
                savedPizza.getPizzatoppingMaps().add(newMapping);
            }
        }

        return orderRepository.save(savedOrder);
    }
}
