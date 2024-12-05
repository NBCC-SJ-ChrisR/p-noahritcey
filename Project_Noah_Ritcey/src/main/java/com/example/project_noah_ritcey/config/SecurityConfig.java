package com.example.project_noah_ritcey.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/employeeDashboard/**").hasRole("EMPLOYEE")
                        .requestMatchers("/buildPizza/**", "/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
                                response.sendRedirect("/employeeDashboard");
                            } else {
                                response.sendRedirect("/buildPizza");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @PostConstruct
    public void init() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Admin/employee password hash
        String employeePassword = "12345";
        String employeeHash = passwordEncoder.encode(employeePassword);
        System.out.println("UPDATE employee SET password = '" + employeeHash + "' WHERE username = 'admin';");

        // Customer password hash
        String customerPassword = "54321";
        String customerHash = passwordEncoder.encode(customerPassword);
        System.out.println("UPDATE customer SET password = '" + customerHash + "' WHERE email = 'dave@thomas.com';");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
