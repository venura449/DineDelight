package org.example.dine_delight.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static class ProfileForm {
        @NotBlank
        private String name;
        @NotBlank
        @Email
        private String email;
        private String newPassword;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    @GetMapping
    public String view(Model model, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        ProfileForm form = new ProfileForm();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        model.addAttribute("profileForm", form);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String update(@Valid @ModelAttribute("profileForm") ProfileForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails principal,
                         Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        if (userRepository.existsByEmailAndIdNot(form.getEmail(), user.getId())) {
            bindingResult.rejectValue("email", "email.exists", "Email already in use");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }

        user.setName(form.getName());
        user.setEmail(form.getEmail());
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        }
        userRepository.save(user);

        return "redirect:/profile?updated";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        userRepository.deleteById(user.getId());
        return "redirect:/logout";
    }
}


