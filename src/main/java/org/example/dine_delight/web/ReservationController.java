package org.example.dine_delight.web;

import org.example.dine_delight.model.DiningTable;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.UserRepository;
import org.example.dine_delight.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String reservationHome() {
        return "reservations/index";
    }

    @GetMapping("/search")
    public String searchAvailability(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                                     @RequestParam int durationMinutes,
                                     @RequestParam int guests,
                                     Model model) {
        List<DiningTable> available = reservationService.findAvailableTables(date, time, durationMinutes, guests);
        model.addAttribute("availableTables", available);
        model.addAttribute("date", date);
        model.addAttribute("time", time);
        model.addAttribute("durationMinutes", durationMinutes);
        model.addAttribute("guests", guests);
        return "reservations/index";
    }

    @PostMapping("/book")
    public String book(@RequestParam Long tableId,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                       @RequestParam int durationMinutes,
                       @RequestParam int guests,
                       @AuthenticationPrincipal UserDetails principal,
                       Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return reservationService.bookTable(tableId, user, date, time, durationMinutes, guests)
                .map(r -> "redirect:/reservations/my?success")
                .orElse("redirect:/reservations?conflict");
    }

    @GetMapping("/my")
    public String myReservations(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("reservations", reservationService.listUserReservations(user));
        return "reservations/my";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        boolean ok = reservationService.cancelReservation(id, user);
        return ok ? "redirect:/reservations/my?cancelled" : "redirect:/reservations/my?error";
    }
}


