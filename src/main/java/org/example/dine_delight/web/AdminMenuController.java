package org.example.dine_delight.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.dine_delight.model.MenuItem;
import org.example.dine_delight.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/menu")
public class AdminMenuController {

    private final MenuItemRepository menuItemRepository;

    public AdminMenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public static class MenuForm {
        @NotBlank
        private String name;
        private String description;
        @Min(0)
        private Integer priceCents;
        private String photoUrl;
        private boolean available = true;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getPriceCents() { return priceCents; }
        public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("items", menuItemRepository.findAll());
        model.addAttribute("menuForm", new MenuForm());
        return "admin/menu";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("menuForm") MenuForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/menu?error";
        }
        MenuItem item = new MenuItem();
        item.setName(form.getName());
        item.setDescription(form.getDescription());
        item.setPriceCents(form.getPriceCents());
        item.setPhotoUrl(form.getPhotoUrl());
        item.setAvailable(form.isAvailable());
        menuItemRepository.save(item);
        return "redirect:/admin/menu?created";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid MenuForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/menu?error";
        }
        MenuItem item = menuItemRepository.findById(id).orElseThrow();
        item.setName(form.getName());
        item.setDescription(form.getDescription());
        item.setPriceCents(form.getPriceCents());
        item.setPhotoUrl(form.getPhotoUrl());
        item.setAvailable(form.isAvailable());
        menuItemRepository.save(item);
        return "redirect:/admin/menu?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        menuItemRepository.deleteById(id);
        return "redirect:/admin/menu?deleted";
    }
}


