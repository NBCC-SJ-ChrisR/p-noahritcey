package com.example.project_noah_ritcey.controllers;

import com.example.project_noah_ritcey.entities.*;

import com.example.project_noah_ritcey.objects.CartPizza;
import com.example.project_noah_ritcey.objects.CartTime;
import com.example.project_noah_ritcey.repositories.PizzacrustRepository;
import com.example.project_noah_ritcey.repositories.PizzasizeRepository;
import com.example.project_noah_ritcey.repositories.PizzatoppingRepository;
import com.example.project_noah_ritcey.services.CustomerService;
import com.example.project_noah_ritcey.services.OrderService;
import com.example.project_noah_ritcey.services.PizzaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buildPizza")
public class PizzaOrderController {
    private final PizzaService pizzaService;
    private final PizzasizeRepository pizzasizeRepository;
    private final PizzacrustRepository pizzacrustRepository;
    private final PizzatoppingRepository pizzatoppingRepository;
    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public String showBuildPizza(Model model, HttpSession session) {
        // Store customer email in session if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            session.setAttribute("customerEmail", auth.getName());
        }

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

        BigDecimal subtotal = pizzaService.calculateCartSubTotal(cart);
        BigDecimal tax = pizzaService.calculateTax(subtotal);
        BigDecimal total = pizzaService.calculateTotalPrice(subtotal);

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
            pizza.setTotalPrice(pizza.getPriceEach().multiply(BigDecimal.valueOf(quantity)));
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

    @GetMapping("/selectTime")
    public String selectTime(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");
        if(cart == null || cart.isEmpty()) {
            return "redirect:/buildPizza/cart";
        }

        LocalTime now = LocalTime.now();
        LocalTime formattedNow = LocalTime.of(now.getHour(), now.getMinute());
        int prepTime = 15 + cart.stream().mapToInt(CartPizza::getQuantity).sum() * 2;
        LocalTime estimatedTime = formattedNow.plusMinutes(prepTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        model.addAttribute("timeSelection", new CartTime());
        model.addAttribute("estimatedTime", estimatedTime.format(formatter));
        model.addAttribute("timeSlots", generateTimes(estimatedTime));

        return "selectTime";
    }

    @PostMapping("/selectTime")
    public String processTimeSelection(@ModelAttribute("timeSelection") CartTime cartTime,
                                       HttpSession session) {
        // Store orderType as Boolean (true for delivery, false for pickup)
        session.setAttribute("delivery", cartTime.isDelivery());
        session.setAttribute("selectedTime", cartTime.getSelectedTime());
        session.setAttribute("paymentType", cartTime.getPaymentType());

        return "redirect:/buildPizza/orderConfirmation";
    }

    @GetMapping("/orderConfirmation")
    public String showOrderConfirmation(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (List<CartPizza>) session.getAttribute("cart");
        Boolean delivery = (Boolean) session.getAttribute("delivery");
        String selectedTime = (String) session.getAttribute("selectedTime");



        BigDecimal subtotal = pizzaService.calculateCartSubTotal(cart);
        BigDecimal tax = pizzaService.calculateTax(subtotal);
        BigDecimal total = pizzaService.calculateTotalPrice(subtotal);

        String orderTypeDisplay = delivery ? "Delivery" : "Pickup";
        model.addAttribute("cart", cart);
        model.addAttribute("delivery", orderTypeDisplay);
        model.addAttribute("selectedTime", selectedTime);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);

        return "orderConfirmation";
    }

    private List<String> generateTimes(LocalTime startTime) {
        List<String> slots = new ArrayList<>();
        LocalTime currentSlot = startTime;
        LocalTime endTime = LocalTime.of(22, 0);

        // Round up to next 15 minutes
        int minute = currentSlot.getMinute();
        if (minute % 15 != 0) {
            currentSlot = currentSlot.plusMinutes(15 - (minute % 15));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        slots.add(currentSlot.format(formatter));

        currentSlot = currentSlot.plusMinutes(15);  // Move to next slot

        while (currentSlot.isBefore(endTime)) {
            slots.add(currentSlot.format(formatter));
            currentSlot = currentSlot.plusMinutes(15);
        }

        return slots;
    }

    @PostMapping("/placeOrder")
    public String placeOrder(HttpSession session) {
        try {
            String selectedTime = (String) session.getAttribute("selectedTime");
            Boolean delivery = (Boolean) session.getAttribute("delivery");
            String paymentType = (String) session.getAttribute("paymentType");


            @SuppressWarnings("unchecked")
            List<CartPizza> cart = (List<CartPizza>) session.getAttribute("cart");

            // Convert cart items to Pizza entities
            List<Pizza> pizzas = cart.stream().map(cartPizza -> {
                Pizza pizza = new Pizza();
                pizza.setPizzaSize(cartPizza.getPizzaSize());
                pizza.setPizzaCrust(cartPizza.getPizzacrust());
                pizza.setQuantity(cartPizza.getQuantity());
                pizza.setPriceEach(cartPizza.getPriceEach());
                pizza.setTotalPrice(cartPizza.getTotalPrice());

                Set<PizzatoppingMap> toppingMaps = cartPizza.getToppings().stream()
                        .map(topping -> {
                            PizzatoppingMap map = new PizzatoppingMap();
                            map.setPizza(pizza);
                            map.setPizzaTopping(topping);
                            return map;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                pizza.setPizzatoppingMaps(toppingMaps);
                return pizza;
            }).collect(Collectors.toList());



            // Parse delivery time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            LocalTime time = LocalTime.parse(selectedTime, formatter);
            LocalDateTime deliveryTime = LocalDateTime.of(LocalDate.now(), time);

            // Get customer and create order
            Customer customer = customerService.findCustomerByEmail((String) session.getAttribute("customerEmail"));
            Order order = orderService.createOrder(customer, pizzas, deliveryTime, delivery, paymentType);

            // Clear session after successful order
            session.removeAttribute("cart");

            return "redirect:/buildPizza/orderSuccess";

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            return "redirect:/buildPizza/orderConfirmation";
        }
    }

    @GetMapping("/orderSuccess")
    public String showOrderSuccess(Model model, HttpSession session) {
        String selectedTime = (String) session.getAttribute("selectedTime");
        Boolean delivery = (Boolean) session.getAttribute("delivery");

        // Set order type for display
        String orderType = delivery ? "delivery" : "pickup";

        // Add attributes to model
        model.addAttribute("orderType", orderType);
        model.addAttribute("selectedTime", selectedTime);

        // Clear session attributes
        session.removeAttribute("delivery");
        session.removeAttribute("selectedTime");
        session.removeAttribute("paymentType");
        session.removeAttribute("lastOrderId");

        return "orderSuccess";
    }

    @GetMapping("/updateTimes")
    @ResponseBody
    public Map<String, Object> updateTimes(@RequestParam boolean delivery, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartPizza> cart = (ArrayList<CartPizza>) session.getAttribute("cart");

        LocalTime now = LocalTime.now();
        LocalTime formattedNow = LocalTime.of(now.getHour(), now.getMinute());

        // Calculate base prep time: 15 min base + 2 min per pizza
        int prepTime = 15 + cart.stream().mapToInt(CartPizza::getQuantity).sum() * 2;

        // Add delivery time if applicable
        if (delivery) {
            prepTime += 15;
        }

        LocalTime estimatedTime = formattedNow.plusMinutes(prepTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        Map<String, Object> response = new HashMap<>();
        response.put("estimatedTime", estimatedTime.format(formatter));
        response.put("timeSlots", generateTimes(estimatedTime));

        return response;
    }
}
