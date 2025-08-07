package com.example.parastoreb.dto.product;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private boolean available;
}
