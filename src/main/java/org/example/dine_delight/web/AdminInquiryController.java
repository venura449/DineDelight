package org.example.dine_delight.web;

import jakarta.validation.constraints.NotBlank;
import org.example.dine_delight.model.Inquiry;
import org.example.dine_delight.model.InquiryReply;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.InquiryRepository;
import org.example.dine_delight.repository.InquiryReplyRepository;
import org.example.dine_delight.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/inquiries")
public class AdminInquiryController {

    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final UserRepository userRepository;

    public AdminInquiryController(InquiryRepository inquiryRepository, 
                                 InquiryReplyRepository inquiryReplyRepository,
                                 UserRepository userRepository) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryReplyRepository = inquiryReplyRepository;
        this.userRepository = userRepository;
    }

    public static class ReplyForm {
        @NotBlank
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inquiries", inquiryRepository.findAllByOrderByCreatedAtDesc());
        return "admin/inquiries";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails principal) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        User admin = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("replyForm", new ReplyForm());
        model.addAttribute("admin", admin);
        return "admin/inquiry-view";
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable Long id, 
                       @Valid @ModelAttribute("replyForm") ReplyForm form,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal UserDetails principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/inquiries/" + id + "?error";
        }
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        User admin = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        
        InquiryReply reply = new InquiryReply();
        reply.setInquiry(inquiry);
        reply.setReplier(admin);
        reply.setMessage(form.getMessage());
        inquiryReplyRepository.save(reply);
        
        return "redirect:/admin/inquiries/" + id + "?replied";
    }
}
