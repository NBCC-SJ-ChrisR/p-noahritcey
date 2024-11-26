package com.example.project_noah_ritcey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/index","buildPizza","/buildPizza/addToCart","/buildPizza/cart").permitAll()
                        .requestMatchers("/employeeDashboard/**").hasRole("EMPLOYEE")
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/employeeDashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
       return new PasswordEncoder() {
           @Override
           public String encode(CharSequence rawPassword) {
               return rawPassword.toString();
           }

           @Override
           public boolean matches(CharSequence rawPassword, String encodedPassword) {
               return rawPassword.toString().equals(encodedPassword);
           }
       };
    }
}
