package com.example.parastoreb.service;

import com.example.parastoreb.dto.cart.AddToCartRequest;
import com.example.parastoreb.dto.cart.CartItemResponse;
import com.example.parastoreb.dto.cart.UpdateQtyRequest;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> addToCart(AddToCartRequest req);
    List<CartItemResponse> getCart(Long userId);
    CartItemResponse updateQuantity(Long cartItemId, UpdateQtyRequest req);
    void removeItem(Long cartItemId, Long userId);
    void clearCart(Long userId);
}
