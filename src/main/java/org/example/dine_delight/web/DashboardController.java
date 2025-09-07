package org.example.dine_delight.web;

import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.OrderRepository;
import org.example.dine_delight.repository.UserRepository;
import org.example.dine_delight.service.EventService;
import org.example.dine_delight.service.ReservationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ReservationService reservationService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public DashboardController(ReservationService reservationService, EventService eventService, UserRepository userRepository, OrderRepository orderRepository) {
        this.reservationService = reservationService;
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        
        var upcomingReservations = reservationService.getUpcomingReservations(user);
        var upcomingEventBookings = eventService.getUpcomingEventBookings(user);
        var allOrders = orderRepository.findAllByUserOrderByCreatedAtDesc(user);
        
        // Calculate total spent from approved orders
        double totalSpent = allOrders.stream()
                .filter(order -> order.getStatus().name().equals("APPROVED"))
                .mapToDouble(order -> order.getTotalCents() / 100.0)
                .sum();
        
        // Calculate reward points (1 point per LKR spent)
        int rewardPoints = (int) totalSpent;
        
        model.addAttribute("upcomingReservations", upcomingReservations);
        model.addAttribute("upcomingEventBookings", upcomingEventBookings);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("rewardPoints", rewardPoints);
        model.addAttribute("user", user);
        return "dashboard";
    }
}


