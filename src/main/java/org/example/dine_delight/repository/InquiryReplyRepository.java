package org.example.dine_delight.repository;

import org.example.dine_delight.model.Inquiry;
import org.example.dine_delight.model.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
    List<InquiryReply> findAllByInquiryOrderByCreatedAtAsc(Inquiry inquiry);
}
