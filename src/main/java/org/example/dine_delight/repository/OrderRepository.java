package org.example.dine_delight.repository;

import org.example.dine_delight.model.Order;
import org.example.dine_delight.model.OrderStatus;
import org.example.dine_delight.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);
    List<Order> findAllByUserAndStatus(User user, OrderStatus status);
}


