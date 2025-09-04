package org.example.dine_delight.web;

import org.example.dine_delight.model.EventSpace;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.UserRepository;
import org.example.dine_delight.service.EventService;
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
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    public EventController(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String eventHome(Model model) {
        // Set default values for the search form
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("time", LocalTime.of(18, 0)); // 6:00 PM
        model.addAttribute("durationMinutes", 180);
        model.addAttribute("guests", 20);
        model.addAttribute("availableSpaces", null);
        return "events/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                         @RequestParam int durationMinutes,
                         @RequestParam int guests,
                         Model model) {
        List<EventSpace> spaces = eventService.findAvailableSpaces(date, time, durationMinutes, guests);
        model.addAttribute("availableSpaces", spaces);
        model.addAttribute("date", date);
        model.addAttribute("time", time);
        model.addAttribute("durationMinutes", durationMinutes);
        model.addAttribute("guests", guests);
        return "events/index";
    }

    @PostMapping("/request")
    public String request(@RequestParam Long spaceId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                          @RequestParam int durationMinutes,
                          @RequestParam int guests,
                          @RequestParam(required = false) String services,
                          @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return eventService.createRequest(spaceId, user, date, time, durationMinutes, guests, services)
                .map(b -> "redirect:/events/my?submitted")
                .orElse("redirect:/events?conflict");
    }

    @GetMapping("/my")
    public String my(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("bookings", eventService.userBookings(user));
        return "events/my";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam Long id,
                           @AuthenticationPrincipal UserDetails principal,
                           Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("booking", eventService.userBookings(user).stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null));
        return "events/edit";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam Long id,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                       @RequestParam int durationMinutes,
                       @RequestParam int guests,
                       @RequestParam(required = false) String services,
                       @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return eventService.editPending(id, user, date, time, durationMinutes, guests, services)
                .map(b -> "redirect:/events/my?updated")
                .orElse("redirect:/events/my?error");
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        boolean ok = eventService.deleteApproved(id, user);
        return ok ? "redirect:/events/my?deleted" : "redirect:/events/my?error";
    }
}


