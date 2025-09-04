package org.example.dine_delight.web;

import org.example.dine_delight.model.DiningTable;
import org.example.dine_delight.repository.DiningTableRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class AdminController {

    private final DiningTableRepository diningTableRepository;

    public AdminController(DiningTableRepository diningTableRepository) {
        this.diningTableRepository = diningTableRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("tables", diningTableRepository.findAll());
        model.addAttribute("tableForm", new DiningTable());
        return "admin/dashboard";
    }

    @PostMapping("/admin/tables")
    public String addTable(@Valid @ModelAttribute("tableForm") DiningTable tableForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tables", diningTableRepository.findAll());
            return "admin/dashboard";
        }
        diningTableRepository.save(tableForm);
        return "redirect:/admin/dashboard?added";
    }
}


