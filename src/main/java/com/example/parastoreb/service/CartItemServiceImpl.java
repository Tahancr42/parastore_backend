package com.example.parastoreb.service;

import com.example.parastoreb.dto.cart.AddToCartRequest;
import com.example.parastoreb.dto.cart.CartItemResponse;
import com.example.parastoreb.dto.cart.UpdateQtyRequest;
import com.example.parastoreb.entity.CartItem;
import com.example.parastoreb.entity.Product;
import com.example.parastoreb.entity.User;
import com.example.parastoreb.repository.CartItemRepository;
import com.example.parastoreb.repository.ProductRepository;
import com.example.parastoreb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    @Override
    public List<CartItemResponse> addToCart(AddToCartRequest req) {
        if (req.getQuantity() <= 0) req.setQuantity(1);

        // charge le user et le produit (relations)
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // si item déjà présent → incrémente la quantité
        CartItem item = cartRepo.findByUser_IdAndProduct_Id(user.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                    return existing;
                })
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setUser(user);          // << relation au lieu de setUserId
                    ci.setProduct(product);    // << relation au lieu de setProductId
                    ci.setQuantity(req.getQuantity());
                    return ci;
                });

        cartRepo.save(item);

        return mapCart(cartRepo.findByUser_Id(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return mapCart(cartRepo.findByUser_Id(userId));
    }

    @Override
    public CartItemResponse updateQuantity(Long cartItemId, UpdateQtyRequest req) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Ligne de panier introuvable"));

        // sécurisation basique : l’item doit appartenir au user
        if (!item.getUser().getId().equals(req.getUserId())) {
            throw new RuntimeException("Accès refusé à ce panier");
        }

        if (req.getQuantity() <= 0) {
            cartRepo.delete(item);
            return null;
        }

        item.setQuantity(req.getQuantity());
        cartRepo.save(item);

        Product product = item.getProduct(); // déjà chargé (ou proxy)
        return mapOne(item, product);
    }

    @Override
    public void removeItem(Long cartItemId, Long userId) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Ligne de panier introuvable"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Accès refusé à ce panier");
        }
        cartRepo.delete(item);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepo.deleteByUser_Id(userId);
    }

    // ----------------- mapping helpers -----------------

    private List<CartItemResponse> mapCart(List<CartItem> items) {
        return items.stream()
                .map(ci -> mapOne(ci, ci.getProduct()))
                .collect(toList());
    }

    private CartItemResponse mapOne(CartItem ci, Product p) {
        double unit = p.getPrice();
        return CartItemResponse.builder()
                .id(ci.getId())
                .productId(p.getId())
                .productName(p.getName())
                .unitPrice(unit)
                .quantity(ci.getQuantity())
                .lineTotal(unit * ci.getQuantity())
                .build();
    }
}
