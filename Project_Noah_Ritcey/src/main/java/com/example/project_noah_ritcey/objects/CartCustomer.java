package com.example.project_noah_ritcey.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;



@Getter
@Setter
@NoArgsConstructor
public class CartCustomer {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Integer houseNumber;
    private String street;
    private String province;
    private String postalCode;
}
