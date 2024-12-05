package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.Customer;
import com.example.project_noah_ritcey.entities.Employee;
import com.example.project_noah_ritcey.repositories.CustomerRepository;
import com.example.project_noah_ritcey.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PizzaUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try employee login first
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee != null) {
            return User.builder()
                    .username(employee.getUsername())
                    .password(employee.getPassword())
                    .roles("EMPLOYEE")
                    .build();
        }

        // Try customer login
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .roles("CUSTOMER")
                .build();
    }
}

