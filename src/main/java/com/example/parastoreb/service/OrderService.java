package com.example.parastoreb.service;

import com.example.parastoreb.dto.order.OrderRequest;
import com.example.parastoreb.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    // créer à partir d’un payload explicite (items {productId, quantity})
    OrderResponse createOrder(OrderRequest request);

    // créer en prenant tous les CartItems d’un user, puis vider le panier
    OrderResponse createOrderFromCart(Long userId);

    // lecture / admin
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByUser(Long userId);
    OrderResponse getOrderById(Long id);
    OrderResponse updateStatus(Long id, String status);
    void deleteOrder(Long id);
}
