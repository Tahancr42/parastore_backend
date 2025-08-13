package com.example.parastoreb.service;

import com.example.parastoreb.dto.order.OrderItemRequest;
import com.example.parastoreb.dto.order.OrderItemResponse;
import com.example.parastoreb.dto.order.OrderRequest;
import com.example.parastoreb.dto.order.OrderResponse;
import com.example.parastoreb.entity.Order;
import com.example.parastoreb.entity.OrderItem;
import com.example.parastoreb.entity.Product;
import com.example.parastoreb.repository.OrderRepository;
import com.example.parastoreb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
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

        // 3) Calculer le total à partir des snapshots
        double total = items.stream()
                .mapToDouble(i -> i.getUnitPriceSnapshot() * i.getQuantity())
                .sum();
        order.setTotalPrice(total);

        // 4) Sauvegarder (cascade ALL attendu sur Order.items)
        Order saved = orderRepository.save(order);

        // 5) Retourner la réponse mappée
        return mapToResponse(saved);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
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

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .items(itemDTOs) // <-- maintenant types alignés
                .build();
    }

}
