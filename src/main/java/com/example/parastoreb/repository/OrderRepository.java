package com.example.parastoreb.repository;

import com.example.parastoreb.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId); // << nécessaire pour getOrdersByUser
}
