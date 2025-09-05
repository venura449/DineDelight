package org.example.dine_delight.web;

import org.example.dine_delight.model.User;
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

    public DashboardController(ReservationService reservationService, EventService eventService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("upcomingReservations", reservationService.getUpcomingReservations(user));
        model.addAttribute("upcomingEventBookings", eventService.getUpcomingEventBookings(user));
        model.addAttribute("user", user);
        return "dashboard";
    }
}


