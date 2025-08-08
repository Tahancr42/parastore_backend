package com.example.parastoreb.service;

import com.example.parastoreb.dto.order.OrderRequest;
import com.example.parastoreb.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
    void deleteOrder(Long id);
}
