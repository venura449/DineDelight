package org.example.dine_delight.web;

import org.example.dine_delight.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listPending(Model model) {
        model.addAttribute("bookings", eventService.pendingBookings());
        return "admin/events";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long id) {
        boolean ok = eventService.approve(id);
        return ok ? "redirect:/admin/events?approved" : "redirect:/admin/events?conflict";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long id) {
        eventService.reject(id);
        return "redirect:/admin/events?rejected";
    }
}


