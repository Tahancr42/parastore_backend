package com.example.parastoreb.mapper;

import com.example.parastoreb.dto.order.OrderItemResponse;
import com.example.parastoreb.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())                     // lien informatif
                .productName(item.getProductNameSnapshot())               // snapshot
                .unitPrice(item.getUnitPriceSnapshot())                   // snapshot
                .quantity(item.getQuantity())
                .lineTotal(item.getUnitPriceSnapshot() * item.getQuantity())
                .build();
    }
}
