package org.example.dine_delight.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inquiry_replies")
public class InquiryReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Inquiry inquiry;

    @ManyToOne(optional = false)
    private User replier;

    @Column(nullable = false, length = 2000)
    private String message;

    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Inquiry getInquiry() { return inquiry; }
    public void setInquiry(Inquiry inquiry) { this.inquiry = inquiry; }
    public User getReplier() { return replier; }
    public void setReplier(User replier) { this.replier = replier; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
