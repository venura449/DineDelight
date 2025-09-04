package org.example.dine_delight.service;

import org.example.dine_delight.model.*;
import org.example.dine_delight.repository.EventBookingRepository;
import org.example.dine_delight.repository.EventSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventSpaceRepository eventSpaceRepository;
    private final EventBookingRepository eventBookingRepository;

    public EventService(EventSpaceRepository eventSpaceRepository, EventBookingRepository eventBookingRepository) {
        this.eventSpaceRepository = eventSpaceRepository;
        this.eventBookingRepository = eventBookingRepository;
    }

    public List<EventSpace> findAvailableSpaces(LocalDate date, LocalTime startTime, int durationMinutes, int guestCount) {
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = start.plusMinutes(durationMinutes);
        return eventSpaceRepository.findAll().stream()
                .filter(s -> s.getCapacity() >= guestCount)
                .filter(s -> {
                    List<EventBooking> overlaps = eventBookingRepository.findOverlaps(s, start, end);
                    return overlaps == null || overlaps.isEmpty();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<EventBooking> createRequest(Long spaceId, User user, LocalDate date, LocalTime startTime, int durationMinutes, int guestCount, String services) {
        EventSpace space = eventSpaceRepository.findById(spaceId).orElse(null);
        if (space == null || space.getCapacity() < guestCount) {
            return Optional.empty();
        }
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = start.plusMinutes(durationMinutes);
        
        // Check for space conflicts
        List<EventBooking> spaceOverlaps = eventBookingRepository.findOverlaps(space, start, end);
        if (spaceOverlaps != null && !spaceOverlaps.isEmpty()) {
            return Optional.empty();
        }
        
        // Check for location conflicts
        List<EventBooking> locationOverlaps = eventBookingRepository.findApprovedOverlapsAtLocation(space.getLocation(), start, end);
        if (locationOverlaps != null && !locationOverlaps.isEmpty()) {
            return Optional.empty();
        }
        
        EventBooking booking = new EventBooking();
        booking.setEventSpace(space);
        booking.setUser(user);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setGuestCount(guestCount);
        booking.setServices(services);
        booking.setStatus(BookingStatus.PENDING);
        return Optional.of(eventBookingRepository.save(booking));
    }

    @Transactional
    public boolean approve(Long bookingId) {
        return eventBookingRepository.findById(bookingId)
                .map(b -> {
                    // Ensure still no overlap with other APPROVED bookings
                    List<EventBooking> overlaps = eventBookingRepository.findOverlaps(b.getEventSpace(), b.getStartTime(), b.getEndTime());
                    boolean clashes = overlaps != null && overlaps.stream()
                            .anyMatch(other -> !other.getId().equals(b.getId()) && other.getStatus() == BookingStatus.APPROVED);
                    if (clashes) return false;
                    
                    // also block by location: if another approved booking exists at the same location
                    List<EventBooking> locationOverlaps = eventBookingRepository.findApprovedOverlapsAtLocation(
                            b.getEventSpace().getLocation(), b.getStartTime(), b.getEndTime());
                    boolean locationClash = locationOverlaps != null && locationOverlaps.stream()
                            .anyMatch(other -> !other.getId().equals(b.getId()));
                    if (locationClash) return false;
                    
                    b.setStatus(BookingStatus.APPROVED);
                    eventBookingRepository.save(b);
                    return true;
                }).orElse(false);
    }

    @Transactional
    public boolean reject(Long bookingId) {
        return eventBookingRepository.findById(bookingId)
                .map(b -> { b.setStatus(BookingStatus.REJECTED); eventBookingRepository.save(b); return true; })
                .orElse(false);
    }

    public List<EventBooking> userBookings(User user) {
        return eventBookingRepository.findByUserOrderByStartTimeDesc(user);
    }

    public List<EventBooking> pendingBookings() {
        return eventBookingRepository.findByStatusOrderByStartTimeAsc(BookingStatus.PENDING);
    }

    @Transactional
    public Optional<EventBooking> editPending(Long bookingId,
                                              User user,
                                              LocalDate date,
                                              LocalTime startTime,
                                              int durationMinutes,
                                              int guestCount,
                                              String services) {
        return eventBookingRepository.findById(bookingId)
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .map(b -> {
                    if (guestCount > b.getEventSpace().getCapacity()) {
                        return null; // capacity exceeded
                    }
                    LocalDateTime start = LocalDateTime.of(date, startTime);
                    LocalDateTime end = start.plusMinutes(durationMinutes);
                    
                    // Check for space conflicts
                    List<EventBooking> spaceOverlaps = eventBookingRepository.findOverlaps(b.getEventSpace(), start, end);
                    boolean spaceClash = spaceOverlaps != null && spaceOverlaps.stream()
                            .anyMatch(other -> !other.getId().equals(b.getId()));
                    if (spaceClash) return null;
                    
                    // Check for location conflicts
                    List<EventBooking> locationOverlaps = eventBookingRepository.findApprovedOverlapsAtLocation(b.getEventSpace().getLocation(), start, end);
                    boolean locationClash = locationOverlaps != null && !locationOverlaps.isEmpty();
                    if (locationClash) return null;
                    
                    b.setStartTime(start);
                    b.setEndTime(end);
                    b.setGuestCount(guestCount);
                    b.setServices(services);
                    return eventBookingRepository.save(b);
                });
    }

    @Transactional
    public boolean deleteApproved(Long bookingId, User user) {
        return eventBookingRepository.findById(bookingId)
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .map(b -> { eventBookingRepository.delete(b); return true; })
                .orElse(false);
    }
}


