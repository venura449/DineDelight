package org.example.dine_delight.repository;

import org.example.dine_delight.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByAvailableTrueOrderByNameAsc();
}


