package org.example.dine_delight.repository;

import org.example.dine_delight.model.BookingStatus;
import org.example.dine_delight.model.EventBooking;
import org.example.dine_delight.model.EventSpace;
import org.example.dine_delight.model.EventLocation;
import org.example.dine_delight.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {

    @Query("select b from EventBooking b where b.eventSpace = :space and b.status <> 'REJECTED' and b.endTime > :start and b.startTime < :end")
    List<EventBooking> findOverlaps(@Param("space") EventSpace space,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("select b from EventBooking b where b.eventSpace.location = :location and b.status = 'APPROVED' and b.endTime > :start and b.startTime < :end")
    List<EventBooking> findApprovedOverlapsAtLocation(@Param("location") EventLocation location,
                                                      @Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    List<EventBooking> findByUserOrderByStartTimeDesc(User user);

    List<EventBooking> findByStatusOrderByStartTimeAsc(BookingStatus status);
    
    @Query("select b from EventBooking b where b.user = :user and b.startTime > :now and b.status = 'APPROVED' order by b.startTime asc")
    List<EventBooking> findUpcomingByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}


