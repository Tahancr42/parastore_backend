package com.example.parastoreb.controller;

import com.example.parastoreb.dto.order.OrderRequest;
import com.example.parastoreb.dto.order.OrderResponse;
import com.example.parastoreb.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse create(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    // depuis le panier (exploite CartItem et vide le panier)
    @PostMapping("/from-cart/{userId}")
    public OrderResponse createFromCart(@PathVariable Long userId) {
        return orderService.createOrderFromCart(userId);
    }

    // admin
    @GetMapping
    public List<OrderResponse> all() {
        return orderService.getAllOrders();
    }

    // “mes commandes”
    @GetMapping("/by-user/{userId}")
    public List<OrderResponse> byUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{id}")
    public OrderResponse one(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
