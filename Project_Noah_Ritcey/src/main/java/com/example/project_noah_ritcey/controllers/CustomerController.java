package com.example.project_noah_ritcey.controllers;

import com.example.project_noah_ritcey.entities.Customer;
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
@RequestMapping("/checkout")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @GetMapping
    public String showAddCustomerDetails(Model model, HttpSession session) {
        if(session.getAttribute("cartCustomer") == null) {
            model.addAttribute("cartCustomer", new CartCustomer());
        }
        return "addCustomerDetails";
    }
}
