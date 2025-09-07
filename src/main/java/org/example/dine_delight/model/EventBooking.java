package org.example.dine_delight.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_booking")
public class EventBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "space_id")
    private EventSpace eventSpace;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int guestCount;

    @Column(length = 512)
    private String services; // comma-separated for simplicity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false)
    private int totalPriceCents;

    @Column(nullable = false)
    private int advancePaymentCents;

    @Column(nullable = false)
    private boolean advancePaymentPaid = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EventSpace getEventSpace() { return eventSpace; }
    public void setEventSpace(EventSpace eventSpace) { this.eventSpace = eventSpace; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public int getTotalPriceCents() { return totalPriceCents; }
    public void setTotalPriceCents(int totalPriceCents) { this.totalPriceCents = totalPriceCents; }
    public int getAdvancePaymentCents() { return advancePaymentCents; }
    public void setAdvancePaymentCents(int advancePaymentCents) { this.advancePaymentCents = advancePaymentCents; }
    public boolean isAdvancePaymentPaid() { return advancePaymentPaid; }
    public void setAdvancePaymentPaid(boolean advancePaymentPaid) { this.advancePaymentPaid = advancePaymentPaid; }
}


