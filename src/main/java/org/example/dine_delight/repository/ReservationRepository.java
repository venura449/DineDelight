package org.example.dine_delight.repository;

import org.example.dine_delight.model.DiningTable;
import org.example.dine_delight.model.Reservation;
import org.example.dine_delight.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where r.diningTable = :table and r.endTime > :start and r.startTime < :end")
    List<Reservation> findOverlaps(@Param("table") DiningTable table,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    List<Reservation> findByUserOrderByStartTimeDesc(User user);
    
    @Query("select r from Reservation r where r.user = :user and r.startTime > :now order by r.startTime asc")
    List<Reservation> findUpcomingByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}


