package com.example.parastoreb.mapper;

import com.example.parastoreb.dto.order.OrderItemResponse;
import com.example.parastoreb.dto.order.OrderResponse;
import com.example.parastoreb.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper itemMapper;

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(itemMapper::toResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
