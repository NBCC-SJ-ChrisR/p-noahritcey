package com.example.project_noah_ritcey.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;



@Getter
@Setter
@NoArgsConstructor
public class CartTime {
    private boolean delivery;
    private String selectedTime;
    private String paymentType;
}
