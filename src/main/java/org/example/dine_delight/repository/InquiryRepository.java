package org.example.dine_delight.repository;

import org.example.dine_delight.model.Inquiry;
import org.example.dine_delight.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findAllByUserOrderByCreatedAtDesc(User user);
    List<Inquiry> findAllByOrderByCreatedAtDesc();
}
