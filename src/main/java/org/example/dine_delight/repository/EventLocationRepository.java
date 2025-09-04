package org.example.dine_delight.repository;

import org.example.dine_delight.model.EventLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLocationRepository extends JpaRepository<EventLocation, Long> {
}


