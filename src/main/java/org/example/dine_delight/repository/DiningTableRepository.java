package org.example.dine_delight.repository;

import org.example.dine_delight.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DiningTable> findWithLockingById(Long id);
}


