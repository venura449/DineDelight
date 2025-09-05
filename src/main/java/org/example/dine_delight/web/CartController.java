package org.example.dine_delight.web;

import org.example.dine_delight.model.MenuItem;
import org.example.dine_delight.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final MenuItemRepository menuItemRepository;

    public CartController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Object obj = session.getAttribute("CART");
        if (obj == null) {
            Map<Long, Integer> map = new HashMap<>();
            session.setAttribute("CART", map);
            return map;
        }
        return (Map<Long, Integer>) obj;
    }

    @PostMapping("/add")
    public String add(@RequestParam Long id, @RequestParam(defaultValue = "1") Integer qty, HttpSession session) {
        var item = menuItemRepository.findById(id).orElseThrow();
        if (!item.isAvailable()) {
            return "redirect:/menu?unavailable";
        }
        var cart = getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + Math.max(1, qty));
        return "redirect:/cart";
    }

    @GetMapping
    public String view(Model model, HttpSession session) {
        var cart = getCart(session);
        int subtotal = 0;
        Map<MenuItem, Integer> lines = new HashMap<>();
        for (var e : cart.entrySet()) {
            var item = menuItemRepository.findById(e.getKey()).orElseThrow();
            int qty = e.getValue();
            lines.put(item, qty);
            subtotal += item.getPriceCents() * qty;
        }
        model.addAttribute("lines", lines);
        model.addAttribute("subtotal", subtotal);
        return "cart/index";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long id, @RequestParam Integer qty, HttpSession session) {
        var cart = getCart(session);
        if (qty == null || qty <= 0) cart.remove(id); else cart.put(id, qty);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session) {
        session.removeAttribute("CART");
        return "redirect:/cart";
    }
}


