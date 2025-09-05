package org.example.dine_delight.web;

import jakarta.validation.constraints.NotBlank;
import org.example.dine_delight.model.Inquiry;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.InquiryRepository;
import org.example.dine_delight.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/inquiries")
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public InquiryController(InquiryRepository inquiryRepository, UserRepository userRepository) {
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
    }

    public static class InquiryForm {
        @NotBlank
        private String subject;
        @NotBlank
        private String message;

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("inquiries", inquiryRepository.findAllByUserOrderByCreatedAtDesc(user));
        model.addAttribute("inquiryForm", new InquiryForm());
        return "inquiries/index";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("inquiryForm") InquiryForm form, 
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UserDetails principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/inquiries?error";
        }
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setSubject(form.getSubject());
        inquiry.setMessage(form.getMessage());
        inquiryRepository.save(inquiry);
        return "redirect:/inquiries?created";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (!inquiry.getUser().getId().equals(user.getId())) {
            return "redirect:/inquiries?unauthorized";
        }
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("inquiryForm", new InquiryForm());
        return "inquiries/view";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, 
                        @Valid @ModelAttribute("inquiryForm") InquiryForm form,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UserDetails principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/inquiries/" + id + "?error";
        }
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (!inquiry.getUser().getId().equals(user.getId())) {
            return "redirect:/inquiries?unauthorized";
        }
        inquiry.setSubject(form.getSubject());
        inquiry.setMessage(form.getMessage());
        inquiryRepository.save(inquiry);
        return "redirect:/inquiries/" + id + "?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (!inquiry.getUser().getId().equals(user.getId())) {
            return "redirect:/inquiries?unauthorized";
        }
        inquiryRepository.delete(inquiry);
        return "redirect:/inquiries?deleted";
    }
}
