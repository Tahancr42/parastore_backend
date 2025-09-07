package com.example.parastoreb.dto.cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long id;           // id du CartItem
    private Long productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double lineTotal;  // unitPrice * quantity
}
