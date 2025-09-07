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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Controller
public class AdminController {

    private final DiningTableRepository diningTableRepository;

    public AdminController(DiningTableRepository diningTableRepository) {
        this.diningTableRepository = diningTableRepository;
    }

    public static class TableForm {
        @NotBlank
        private String tableCode;

        @NotNull
        @Min(1)
        private Integer capacity;

        @NotNull
        @Min(0)
        private Integer pricePerPersonPerHourLkr; // Price in LKR (not cents)

        public String getTableCode() { return tableCode; }
        public void setTableCode(String tableCode) { this.tableCode = tableCode; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public Integer getPricePerPersonPerHourLkr() { return pricePerPersonPerHourLkr; }
        public void setPricePerPersonPerHourLkr(Integer pricePerPersonPerHourLkr) { this.pricePerPersonPerHourLkr = pricePerPersonPerHourLkr; }
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("tables", diningTableRepository.findAll());
        model.addAttribute("tableForm", new TableForm());
        return "admin/dashboard";
    }

    @PostMapping("/admin/tables")
    public String addTable(@Valid @ModelAttribute("tableForm") TableForm tableForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tables", diningTableRepository.findAll());
            return "admin/dashboard";
        }
        
        DiningTable table = new DiningTable();
        table.setTableCode(tableForm.getTableCode());
        table.setCapacity(tableForm.getCapacity());
        // Convert LKR to cents
        table.setPricePerPersonPerHourCents(tableForm.getPricePerPersonPerHourLkr() * 100);
        
        diningTableRepository.save(table);
        return "redirect:/admin/dashboard?added";
    }
}


