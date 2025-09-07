package com.example.parastoreb.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;                // id de l'OrderItem
    private Long productId;         // id du produit d'origine
    private String productName;     // snapshot au moment de l'achat
    private double unitPrice;       // snapshot au moment de l'achat
    private int quantity;
    private double lineTotal;       // unitPrice * quantity
}
