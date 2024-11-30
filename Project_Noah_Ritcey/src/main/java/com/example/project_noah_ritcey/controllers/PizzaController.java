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
import java.util.stream.Collectors;

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

        if (session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<CartPizza>());
        }
        model.addAttribute("sizes", pizzaService.getAllPizzasize());
        model.addAttribute("crusts", pizzaService.getAllPizzacrust());
        model.addAttribute("toppings", pizzaService.getActiveToppings());
        model.addAttribute("isEditing", false);
        return "buildPizza";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Integer sizeId, @RequestParam Integer crustId,
                            @RequestParam(required = false) List<Integer> toppingsIds,
                            @RequestParam Integer quantity, HttpSession session) {

        try {
            Pizzasize size = pizzasizeRepository.findById(sizeId).orElseThrow(() -> new RuntimeException("Size not found"));
            Pizzacrust crust = pizzacrustRepository.findById(crustId).orElseThrow(() -> new RuntimeException("Crust not found"));

            List<Pizzatopping> toppings = new ArrayList<>();
            if (toppingsIds != null && !toppingsIds.isEmpty()) {
                toppings = pizzatoppingRepository.findAllById(toppingsIds);
            }

            CartPizza pizza = pizzaService.createCartPizza(size, crust, toppings, quantity);
            @SuppressWarnings("unchecked")
            List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
            cart.add(pizza);
            session.setAttribute("cart", cart);
            return "redirect:/buildPizza";

        } catch (RuntimeException e) {
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
    public String updateCartPizzaQuantity(@RequestParam Integer index, @RequestParam Integer quantity, HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size() && quantity > 0) {
            CartPizza pizza = cart.get(index);
            pizza.setQuantity(quantity);
            pizza.setTotalPrice(pizza.getPriceEach() * quantity);
            session.setAttribute("cart", cart);
        }
        return "redirect:/buildPizza/cart";
    }

    @PostMapping("/cart/remove")
    public String removeCartPizza(@RequestParam Integer index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index.intValue());
            session.setAttribute("cart", cart);
        }
        return "redirect:/buildPizza/cart";
    }

    @GetMapping("/edit/{index}")
    public String editPizza(@PathVariable("index") Integer index, Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (List<CartPizza>) session.getAttribute("cart");

        if (cart != null && index >= 0 && index < cart.size()) {
            CartPizza pizzaToEdit = cart.get(index);

            session.setAttribute("editingPizzaIndex", index);

            // Set editing flag
            model.addAttribute("isEditing", true);

            // Add all available options
            model.addAttribute("sizes", pizzaService.getAllPizzasize());
            model.addAttribute("crusts", pizzaService.getAllPizzacrust());
            model.addAttribute("toppings", pizzaService.getActiveToppings());

            // Set selected values from the pizza being edited
            model.addAttribute("selectedSize", pizzaToEdit.getPizzaSize().getId());
            model.addAttribute("selectedCrust", pizzaToEdit.getPizzacrust().getId());
            model.addAttribute("selectedToppings", pizzaToEdit.getToppings().stream()
                    .map(topping -> topping.getId())
                    .collect(Collectors.toList()));
            model.addAttribute("quantity", pizzaToEdit.getQuantity());

            return "buildPizza";
        }

        return "redirect:/buildPizza/cart";
    }

    @PostMapping("/update")
    public String updatePizza(@RequestParam Integer sizeId,
                              @RequestParam Integer crustId,
                              @RequestParam(required = false) List<Integer> toppingsIds,
                              @RequestParam Integer quantity,
                              HttpSession session) {
        try {
            // Get editing index and cart from session
            Integer editingIndex = (Integer) session.getAttribute("editingPizzaIndex");
            if (editingIndex == null) {
                return "redirect:/buildPizza/cart";
            }

            // Get size, crust, and toppings
            Pizzasize size = pizzasizeRepository.findById(sizeId)
                    .orElseThrow(() -> new RuntimeException("Size not found"));
            Pizzacrust crust = pizzacrustRepository.findById(crustId)
                    .orElseThrow(() -> new RuntimeException("Crust not found"));

            List<Pizzatopping> toppings = new ArrayList<>();
            if (toppingsIds != null && !toppingsIds.isEmpty()) {
                toppings = pizzatoppingRepository.findAllById(toppingsIds);
            }

            // Create updated pizza
            CartPizza updatedPizza = pizzaService.createCartPizza(size, crust, toppings, quantity);

            // Update pizza in cart
            @SuppressWarnings("unchecked")
            List<CartPizza> cart = (List<CartPizza>) session.getAttribute("cart");
            if (cart != null && editingIndex >= 0 && editingIndex < cart.size()) {
                cart.set(editingIndex, updatedPizza);
                session.setAttribute("cart", cart);
            }

            // Clear editing state
            session.removeAttribute("editingPizzaIndex");

            return "redirect:/buildPizza/cart";

        } catch (RuntimeException e) {
            return "redirect:/buildPizza/cart?error=" + e.getMessage();
        }
    }
}
