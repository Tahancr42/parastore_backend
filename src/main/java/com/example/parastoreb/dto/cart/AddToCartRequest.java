package com.example.parastoreb.dto.cart;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long userId;
    private Long productId;
    private int quantity; // quantité à ajouter (>=1)
}
