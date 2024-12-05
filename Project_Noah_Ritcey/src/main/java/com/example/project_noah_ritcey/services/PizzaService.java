package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.objects.CartPizza;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzacrustRepository pizzacrustRepository;
    private final PizzatoppingRepository pizzatoppingRepository;
    private final PizzaToppingMapRepository toppingMapRepository;
    private final PizzasizeRepository pizzasizeRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.15");
    private static final BigDecimal TAX_MULTIPLIER = new BigDecimal("1.15");
    private static final int DECIMAL_PLACES = 2;

    public List<Pizzasize> getAllPizzasize() {
        return pizzasizeRepository.findAll();
    }

    public List<Pizzatopping> getAllPizzatopping() {
        return pizzatoppingRepository.findAll();
    }

    public List<Pizzacrust> getAllPizzacrust() {
        return pizzacrustRepository.findAll();
    }

    public List<Pizzatopping> getActiveToppings() {
        return pizzatoppingRepository.findByIsActive((byte)1);
    }

    public CartPizza createCartPizza(Pizzasize size, Pizzacrust crust, List<Pizzatopping> toppings, int quantity) {
        CartPizza cartPizza = new CartPizza();
        cartPizza.setPizzacrust(crust);
        cartPizza.setPizzaSize(size);
        cartPizza.setQuantity(quantity);
        cartPizza.setToppings(new ArrayList<>(toppings));

        BigDecimal basePrice = size.getPrice().add(crust.getPrice());

        BigDecimal toppingPrice = toppings.stream()
                .map(Pizzatopping::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pricePerPizza = basePrice.add(toppingPrice);
        cartPizza.setPriceEach(pricePerPizza);
        cartPizza.setTotalPrice(pricePerPizza.multiply(BigDecimal.valueOf(quantity)));

        return cartPizza;
    }

    public Pizza convertCartPizzaToEntity(CartPizza cartPizza, Order order) {
        Pizza pizza = new Pizza();
        pizza.setOrder(order);
        pizza.setPizzaSize(cartPizza.getPizzaSize());
        pizza.setPizzaCrust(cartPizza.getPizzacrust());
        pizza.setQuantity(cartPizza.getQuantity());
        pizza.setPriceEach(cartPizza.getPriceEach());
        pizza.setTotalPrice(cartPizza.getTotalPrice());
        return pizza;
    }

    public BigDecimal calculatePrice(int sizeId, int crustId, List<Integer> toppingIds) {
        Pizzasize size = pizzasizeRepository.findById(sizeId)
                .orElseThrow(() -> new RuntimeException("Size not found."));
        Pizzacrust crust = pizzacrustRepository.findById(crustId)
                .orElseThrow(() -> new RuntimeException("Crust not found"));
        List<Pizzatopping> toppings = pizzatoppingRepository.findAllById(toppingIds);

        return size.getPrice()
                .add(crust.getPrice())
                .add(toppings.stream()
                        .map(Pizzatopping::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public List<PizzatoppingMap> getPizzaToppings(Integer pizzaId) {
        return toppingMapRepository.findByPizzaId(pizzaId);
    }

    public BigDecimal calculateSubTotal(List<Pizza> pizzas) {
        return pizzas.stream()
                .map(Pizza::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateCartSubTotal(List<CartPizza> cartPizzas) {
        if (cartPizzas == null) return BigDecimal.ZERO;
        return cartPizzas.stream()
                .map(CartPizza::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTax(BigDecimal subTotal) {
        return subTotal.multiply(TAX_RATE)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalPrice(BigDecimal subtotal) {
        return subtotal.multiply(TAX_MULTIPLIER)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}