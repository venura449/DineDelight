package org.example.dine_delight.web;

import org.example.dine_delight.model.Order;
import org.example.dine_delight.model.OrderStatus;
import org.example.dine_delight.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepository;

    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
        return "redirect:/admin/orders?approved";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
        return "redirect:/admin/orders?rejected";
    }

    @PostMapping("/{id}/dispatch")
    public String dispatch(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.DISPATCHED);
        orderRepository.save(order);
        return "redirect:/admin/orders?dispatched";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/orders?deleted";
    }
}


