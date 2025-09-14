package com.example.parastoreb.service;

import com.example.parastoreb.dto.order.OrderItemResponse;
import com.example.parastoreb.dto.order.OrderRequest;
import com.example.parastoreb.dto.order.OrderResponse;
import com.example.parastoreb.entity.CartItem;
import com.example.parastoreb.entity.Order;
import com.example.parastoreb.entity.OrderItem;
import com.example.parastoreb.entity.Product;
import com.example.parastoreb.repository.CartItemRepository;
import com.example.parastoreb.repository.OrderRepository;
import com.example.parastoreb.repository.ProductRepository;
import com.example.parastoreb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository; // << nécessaire pour from-cart
    private final UserRepository userRepository; // << pour récupérer l'email

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1) Créer la commande
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // 2) Transformer les OrderItemRequest -> OrderItem avec snapshots
        List<OrderItem> items = request.getItems().stream().map(reqItem -> {
            Product product = productRepository.findById(reqItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable: " + reqItem.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);                              // relation
            item.setProduct(product);                          // lien référentiel
            item.setProductNameSnapshot(product.getName());    // snapshot
            item.setUnitPriceSnapshot(product.getPrice());     // snapshot
            item.setQuantity(reqItem.getQuantity());
            return item;
        }).toList();

        order.setItems(items);

        // 3) Total
        double total = items.stream()
                .mapToDouble(i -> i.getUnitPriceSnapshot() * i.getQuantity())
                .sum();
        order.setTotalPrice(total);

        // 4) Sauvegarde
        Order saved = orderRepository.save(order);

        // 5) Réponse
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        // Récupérer le panier
        List<CartItem> cartItems = cartItemRepository.findByUser_Id(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Le panier est vide, impossible de créer une commande.");
        }

        // Créer la commande
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Convertir CartItem -> OrderItem (snapshots)
        List<OrderItem> items = cartItems.stream().map(ci -> {
            Product p = ci.getProduct();
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setProductNameSnapshot(p.getName());
            oi.setUnitPriceSnapshot(p.getPrice());
            oi.setQuantity(ci.getQuantity());
            return oi;
        }).toList();

        order.setItems(items);

        double total = items.stream()
                .mapToDouble(i -> i.getUnitPriceSnapshot() * i.getQuantity())
                .sum();
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        // Vider le panier
        cartItemRepository.deleteByUser_Id(userId);

        return mapToResponse(saved);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        order.setStatus(status); // TODO: valider transitions si tu as un Enum
        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // ---------------- Mapping helpers ----------------

    private OrderResponse mapToResponse(Order order) {
        var itemDTOs = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProductNameSnapshot())
                        .unitPrice(i.getUnitPriceSnapshot())
                        .quantity(i.getQuantity())
                        .lineTotal(i.getUnitPriceSnapshot() * i.getQuantity())
                        .build())
                .toList();

        // Récupérer les informations de l'utilisateur
        var user = userRepository.findById(order.getUserId());
        String userEmail = user.map(u -> u.getEmail()).orElse("N/A");
        String userName = user.map(u -> u.getName()).orElse("N/A");
        String userPhone = user.map(u -> u.getPhone()).orElse("N/A");

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userEmail(userEmail)
                .userName(userName)
                .userPhone(userPhone)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .items(itemDTOs)
                .build();
    }
}
