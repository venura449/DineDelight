package org.example.dine_delight.web;

import org.example.dine_delight.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kitchen/menu")
public class KitchenMenuController {

    private final MenuItemRepository menuItemRepository;

    public KitchenMenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("items", menuItemRepository.findAll());
        return "kitchen/menu";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        var item = menuItemRepository.findById(id).orElseThrow();
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
        return "redirect:/kitchen/menu";
    }
}


