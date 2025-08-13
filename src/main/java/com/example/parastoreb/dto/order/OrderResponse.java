// src/main/java/com/example/parastoreb/dto/order/OrderResponse.java
package com.example.parastoreb.dto.order;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private double totalPrice;
    private LocalDateTime createdAt;

    // IMPORTANT: la réponse contient des OrderItemResponse
    private List<OrderItemResponse> items;
}
