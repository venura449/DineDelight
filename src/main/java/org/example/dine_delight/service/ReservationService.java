package org.example.dine_delight.service;

import org.example.dine_delight.model.DiningTable;
import org.example.dine_delight.model.Reservation;
import org.example.dine_delight.model.User;
import org.example.dine_delight.repository.DiningTableRepository;
import org.example.dine_delight.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final DiningTableRepository diningTableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(DiningTableRepository diningTableRepository, ReservationRepository reservationRepository) {
        this.diningTableRepository = diningTableRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<DiningTable> findAvailableTables(LocalDate date, LocalTime startTime, int durationMinutes, int guestCount) {
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<DiningTable> suitableTables = diningTableRepository.findAll().stream()
                .filter(t -> t.getCapacity() >= guestCount)
                .collect(Collectors.toList());

        return suitableTables.stream()
                .filter(t -> reservationRepository.findOverlaps(t, start, end).isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Reservation> bookTable(Long tableId, User user, LocalDate date, LocalTime startTime, int durationMinutes, int guestCount) {
        DiningTable table = diningTableRepository.findWithLockingById(tableId).orElse(null);
        if (table == null || table.getCapacity() < guestCount) {
            return Optional.empty();
        }
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = start.plusMinutes(durationMinutes);
        boolean hasOverlap = !reservationRepository.findOverlaps(table, start, end).isEmpty();
        if (hasOverlap) {
            return Optional.empty();
        }
        Reservation reservation = new Reservation();
        reservation.setDiningTable(table);
        reservation.setUser(user);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setGuestCount(guestCount);
        return Optional.of(reservationRepository.save(reservation));
    }

    public List<Reservation> listUserReservations(User user) {
        return reservationRepository.findByUserOrderByStartTimeDesc(user);
    }

    public List<Reservation> getUpcomingReservations(User user) {
        return reservationRepository.findUpcomingByUser(user, LocalDateTime.now());
    }

    @Transactional
    public boolean cancelReservation(Long reservationId, User user) {
        return reservationRepository.findById(reservationId)
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .map(r -> { reservationRepository.delete(r); return true; })
                .orElse(false);
    }
}


