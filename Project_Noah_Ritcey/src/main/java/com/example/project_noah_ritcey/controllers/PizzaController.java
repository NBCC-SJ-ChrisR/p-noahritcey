package com.example.project_noah_ritcey.controllers;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.objects.CartPizza;
import com.example.project_noah_ritcey.repositories.PizzacrustRepository;
import com.example.project_noah_ritcey.repositories.PizzasizeRepository;
import com.example.project_noah_ritcey.repositories.PizzatoppingRepository;
import com.example.project_noah_ritcey.services.PizzaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buildPizza")
public class PizzaController {
    private final PizzaService pizzaService;
    private final PizzasizeRepository pizzasizeRepository;
    private final PizzacrustRepository pizzacrustRepository;
    private final PizzatoppingRepository pizzatoppingRepository;

    @GetMapping
    public String showBuildPizza(Model model, HttpSession session) {

        if(session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<CartPizza>());
        }
        model.addAttribute("sizes", pizzaService.getAllPizzasize());
        model.addAttribute("crusts", pizzaService.getAllPizzacrust());
        model.addAttribute("toppings", pizzaService.getActiveToppings());
        return "buildPizza";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Integer sizeId, @RequestParam Integer crustId,
                            @RequestParam(required = false) List<Integer> toppingsIds,
                            @RequestParam Integer quantity, HttpSession session) {

        try{
            Pizzasize size = pizzasizeRepository.findById(sizeId).orElseThrow(() -> new RuntimeException("Size not found"));
            Pizzacrust crust = pizzacrustRepository.findById(crustId).orElseThrow(() -> new RuntimeException("Crust not found"));

            List<Pizzatopping> toppings = new ArrayList<>();
            if(toppingsIds != null && !toppingsIds.isEmpty()) {
                toppings = pizzatoppingRepository.findAllById(toppingsIds);
            }

            CartPizza pizza = pizzaService.createCartPizza(size, crust, toppings, quantity);
            @SuppressWarnings("unchecked")
            List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
            cart.add(pizza);
            session.setAttribute("cart", cart);
            return "redirect:/buildPizza";

        }catch (RuntimeException e) {
            return "redirect:/buildPizza" + e.getMessage();
        }
    }

    @GetMapping("/cart")
    public String showCart(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (List<CartPizza>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            model.addAttribute("info", "Your cart is empty");
            return "cart";
        }

        Float subtotal = pizzaService.calculateCartSubTotal(cart);
        Float tax = pizzaService.calculateTax(subtotal);
        Float total = pizzaService.calculateTotalPrice(subtotal);

        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/update")
    public String updateCartPizzaQuantity(@RequestParam Integer index,@RequestParam Integer quantity, HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
        if(cart != null && index >=0 && index < cart.size() && quantity > 0) {
            CartPizza pizza = cart.get(index);
            pizza.setQuantity(quantity);
            pizza.setTotalPrice(pizza.getPriceEach() * quantity);
            session.setAttribute("cart", cart);
        }
        return "redirect:/buildPizza/cart";
    }

    @PostMapping("/cart/remove")
    public String removeCartPizza(@RequestParam Integer index,HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
        if(cart != null && index >=0 && index < cart.size()) {
            cart.remove(index.intValue());
            session.setAttribute("cart", cart);
        }
        return "redirect:/buildPizza/cart";
    }
}
