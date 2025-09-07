package org.example.dine_delight.web;

import org.example.dine_delight.model.Reservation;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.ReservationRepository;
import org.example.dine_delight.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Controller
@RequestMapping("/reservations")
public class ReservationPaymentController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationPaymentController(ReservationRepository reservationRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public static class PaymentForm {
        @NotBlank
        @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format: 1234 5678 9012 3456")
        private String cardNumber;

        @NotBlank
        @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiry must be in format: MM/YY")
        private String expiry;

        @NotBlank
        @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
        private String cvv;

        @NotBlank
        private String cardholderName;

        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        public String getExpiry() { return expiry; }
        public void setExpiry(String expiry) { this.expiry = expiry; }
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
        public String getCardholderName() { return cardholderName; }
        public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }
    }

    @GetMapping("/{id}/payment")
    public String paymentPage(@PathVariable Long id, 
                             @AuthenticationPrincipal UserDetails principal, 
                             Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "redirect:/reservations/my?unauthorized";
        }
        
        if (reservation.isAdvancePaymentPaid()) {
            return "redirect:/reservations/my?alreadyPaid";
        }
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("paymentForm", new PaymentForm());
        return "reservations/payment";
    }

    @PostMapping("/{id}/payment")
    public String processPayment(@PathVariable Long id,
                                @Valid @ModelAttribute("paymentForm") PaymentForm form,
                                @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "redirect:/reservations/my?unauthorized";
        }
        
        if (reservation.isAdvancePaymentPaid()) {
            return "redirect:/reservations/my?alreadyPaid";
        }
        
        // Dummy payment validation
        if (!isValidCard(form)) {
            return "redirect:/reservations/" + id + "/payment?invalid";
        }
        
        // Mark payment as paid
        reservation.setAdvancePaymentPaid(true);
        reservationRepository.save(reservation);
        
        return "redirect:/reservations/my?paymentSuccess";
    }
    
    private boolean isValidCard(PaymentForm form) {
        // Dummy validation - accept any card that starts with 4, 5, or 3
        String cardNumber = form.getCardNumber().replaceAll("\\s", "");
        return cardNumber.matches("^[345]\\d{15}$") && 
               form.getExpiry().matches("\\d{2}/\\d{2}") &&
               form.getCvv().matches("\\d{3}") &&
               !form.getCardholderName().trim().isEmpty();
    }
}

