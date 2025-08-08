package com.example.parastoreb.dto.order;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private String productName;
    private int quantity;
    private Double price;
}
