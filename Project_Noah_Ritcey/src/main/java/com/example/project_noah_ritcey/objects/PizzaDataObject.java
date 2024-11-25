package com.example.project_noah_ritcey.objects;

import lombok.Data;

import java.util.List;

@Data
public class PizzaDataObject {
    private int sizeId;
    private int crustId;
    private List<Integer> toppingIds;
    private int quantity;
}
