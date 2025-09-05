package org.example.dine_delight.web;

import org.example.dine_delight.model.Order;
import org.example.dine_delight.model.OrderStatus;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.OrderRepository;
import org.example.dine_delight.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("orders", orderRepository.findAllByUserOrderByCreatedAtDesc(user));
        return "orders/index";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Order order = orderRepository.findById(id).orElseThrow();
        if (order.getUser().getId().equals(user.getId()) && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Order order = orderRepository.findById(id).orElseThrow();
        if (order.getUser().getId().equals(user.getId()) && order.getStatus() == OrderStatus.PENDING) {
            orderRepository.delete(order);
        }
        return "redirect:/orders";
    }
}


