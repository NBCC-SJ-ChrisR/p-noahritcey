package com.example.project_noah_ritcey.controllers;


import com.example.project_noah_ritcey.objects.CartCustomer;

import com.example.project_noah_ritcey.repositories.CustomerRepository;
import com.example.project_noah_ritcey.services.CustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customerInfo")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @GetMapping
    public String showAddCustomerDetails(Model model, HttpSession session) {
        CartCustomer existingCustomer = (CartCustomer) session.getAttribute("cartCustomer");
        model.addAttribute("cartCustomer", existingCustomer != null ? existingCustomer : new CartCustomer());
        return "customerInfo";
    }

    @PostMapping("/submitCustomerInfo")
    public String submitCustomerInfo(@ModelAttribute CartCustomer cartCustomer,
                                     HttpSession session) {
        // Create new customer object and set all fields
        CartCustomer customer = new CartCustomer();
        customer.setFirstName(cartCustomer.getFirstName());
        customer.setLastName(cartCustomer.getLastName());
        customer.setPhoneNumber(cartCustomer.getPhoneNumber());
        customer.setEmail(cartCustomer.getEmail());
        customer.setHouseNumber(cartCustomer.getHouseNumber());
        customer.setStreet(cartCustomer.getStreet());
        customer.setProvince(cartCustomer.getProvince());
        customer.setPostalCode(cartCustomer.getPostalCode());

        // Store in session
        session.setAttribute("cartCustomer", customer);

        return "redirect:/selectTime";
    }
}
