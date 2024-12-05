package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public Order createOrder(Customer customer, List<Pizza> cartPizzas, LocalDateTime deliveryTime, Boolean delivery, String paymentType) {
        System.out.println("Creating order for customer: " + customer.getEmail());
        System.out.println("Delivery time: " + deliveryTime);
        System.out.println("Delivery?: " + delivery);
        System.out.println("Payment type: " + paymentType);
        System.out.println("Number of pizzas: " + cartPizzas.size());

        try {
            // Calculate totals
            BigDecimal subtotal = pizzaService.calculateSubTotal(cartPizzas);
            System.out.println("Subtotal calculated: " + subtotal);
            BigDecimal tax = pizzaService.calculateTax(subtotal);
            BigDecimal total = pizzaService.calculateTotalPrice(subtotal);
            System.out.println("Total calculated: " + total);

            byte byteValueDelivery = (byte) (delivery ? 1 : 0);
            BigDecimal amountPaid = Objects.equals(paymentType, "online") ? total : BigDecimal.ZERO;

            // Create order
            Order order = new Order();
            order.setDelivery(byteValueDelivery);
            order.setCustomer(customer);
            order.setSubTotal(subtotal);
            order.setHst(tax);
            order.setOrderTotal(total);
            order.setAmountPaid(amountPaid);
            order.setOrderStatus("PENDING");
            order.setDeliveryDate(deliveryTime.toInstant(java.time.ZoneOffset.UTC));
            order.setOrderPlacedDate(Instant.now());

            System.out.println("Saving initial order...");
            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved with ID: " + savedOrder.getId());

            // Process pizzas
            for (Pizza cartPizza : cartPizzas) {
                System.out.println("Processing pizza with size: " + cartPizza.getPizzaSize().getName());
                Pizza orderPizza = new Pizza();
                orderPizza.setOrder(savedOrder);
                orderPizza.setPizzaSize(cartPizza.getPizzaSize());
                orderPizza.setPizzaCrust(cartPizza.getPizzaCrust());
                orderPizza.setQuantity(cartPizza.getQuantity());
                orderPizza.setPriceEach(cartPizza.getPriceEach());
                orderPizza.setTotalPrice(cartPizza.getTotalPrice());
                orderPizza.setPizzatoppingMaps(new LinkedHashSet<>());

                System.out.println("Saving pizza...");
                Pizza savedPizza = pizzaRepository.save(orderPizza);
                System.out.println("Pizza saved with ID: " + savedPizza.getId());

                // Process toppings
                for (PizzatoppingMap cartMapping : cartPizza.getPizzatoppingMaps()) {
                    System.out.println("Adding topping: " + cartMapping.getPizzaTopping().getName());
                    PizzatoppingMap newMapping = new PizzatoppingMap();
                    newMapping.setPizza(savedPizza);
                    newMapping.setPizzaTopping(cartMapping.getPizzaTopping());
                    pizzaToppingMapRepository.save(newMapping);
                }
            }

            System.out.println("Saving final order...");
            return orderRepository.save(savedOrder);
        } catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
