package com.example.parastoreb.controller;

import com.example.parastoreb.dto.cart.AddToCartRequest;
import com.example.parastoreb.dto.cart.CartItemResponse;
import com.example.parastoreb.dto.cart.UpdateQtyRequest;
import com.example.parastoreb.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartService;

    // Ajouter au panier (retourne le panier à jour)
    @PostMapping("/add")
    public List<CartItemResponse> add(@RequestBody AddToCartRequest req) {
        return cartService.addToCart(req);
    }

    // Récupérer le panier d'un utilisateur
    @GetMapping
    public List<CartItemResponse> get(@RequestParam Long userId) {
        return cartService.getCart(userId);
    }

    // Modifier la quantité d'une ligne
    @PutMapping("/item/{id}")
    public CartItemResponse update(@PathVariable Long id, @RequestBody UpdateQtyRequest req) {
        return cartService.updateQuantity(id, req);
    }

    // Supprimer une ligne
    @DeleteMapping("/item/{id}")
    public void remove(@PathVariable Long id, @RequestParam Long userId) {
        cartService.removeItem(id, userId);
    }

    // Vider le panier
    @DeleteMapping("/clear")
    public void clear(@RequestParam Long userId) {
        cartService.clearCart(userId);
    }
}
