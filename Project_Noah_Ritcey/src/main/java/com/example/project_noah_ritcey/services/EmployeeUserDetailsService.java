package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.Employee;
import com.example.project_noah_ritcey.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found: " + username));


        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())
                .roles("EMPLOYEE")
                .build();
    }


    public Employee getLoggedInEmployee() {

        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Employee not found for logged-in user: " + username));
        }

        throw new UsernameNotFoundException("No authenticated user found.");
    }
}
