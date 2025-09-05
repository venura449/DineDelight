package org.example.dine_delight.web;

import org.example.dine_delight.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/menu")
    public String viewMenu(Model model,
                           @RequestParam(value = "q", required = false) String query,
                           @RequestParam(value = "min", required = false) Integer min,
                           @RequestParam(value = "max", required = false) Integer max) {
        var all = menuItemRepository.findAllByAvailableTrueOrderByNameAsc();
        var filtered = all.stream()
                .filter(i -> query == null || query.isBlank() ||
                        i.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (i.getDescription() != null && i.getDescription().toLowerCase().contains(query.toLowerCase())))
                .filter(i -> min == null || i.getPriceCents() >= min)
                .filter(i -> max == null || i.getPriceCents() <= max)
                .toList();
        model.addAttribute("items", filtered);
        model.addAttribute("q", query == null ? "" : query);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        return "menu/user";
    }
}


