package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.Customer;
import com.example.project_noah_ritcey.objects.CartCustomer;
import com.example.project_noah_ritcey.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CartCustomer createCartCustomer(String firstName, String lastName, String phoneNumber, String email, Integer houseNumber, String street, String province, String postalCode) {
        CartCustomer cartCustomer = new CartCustomer();
        cartCustomer.setFirstName(firstName);
        cartCustomer.setLastName(lastName);
        cartCustomer.setPhoneNumber(phoneNumber);
        cartCustomer.setEmail(email);
        cartCustomer.setHouseNumber(houseNumber);
        cartCustomer.setStreet(street);
        cartCustomer.setProvince(province);
        cartCustomer.setPostalCode(postalCode);

        return cartCustomer;
    }
}
