package com.example.parastoreb.repository;

import com.example.parastoreb.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // trouve tous les items d’un user (via la relation user.id)
    List<CartItem> findByUser_Id(Long userId);

    // pour éviter les doublons (un produit unique par user)
    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

    // vider le panier d’un user
    void deleteByUser_Id(Long userId);
}
