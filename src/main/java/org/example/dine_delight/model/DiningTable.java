package org.example.dine_delight.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dining_table")
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tableCode;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int pricePerPersonPerHourCents; // Price in cents (e.g., 50000 = LKR 500.00)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPricePerPersonPerHourCents() {
        return pricePerPersonPerHourCents;
    }

    public void setPricePerPersonPerHourCents(int pricePerPersonPerHourCents) {
        this.pricePerPersonPerHourCents = pricePerPersonPerHourCents;
    }
}


