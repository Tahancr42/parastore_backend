package com.example.parastoreb.service;

import com.example.parastoreb.dto.order.*;
import com.example.parastoreb.entity.*;
import com.example.parastoreb.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = request.getItems().stream()
                .map(i -> OrderItem.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setItems(items);
        order.setTotalPrice(items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum());

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemRequest> itemDTOs = order.getItems().stream()
                .map(i -> {
                    OrderItemRequest dto = new OrderItemRequest();
                    dto.setProductId(i.getProductId());
                    dto.setProductName(i.getProductName());
                    dto.setQuantity(i.getQuantity());
                    dto.setPrice(i.getPrice());
                    return dto;
                }).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .items(itemDTOs)
                .build();
    }
}
