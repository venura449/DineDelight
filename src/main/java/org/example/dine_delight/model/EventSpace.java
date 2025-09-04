package org.example.dine_delight.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_space")
public class EventSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id")
    private EventLocation location;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public EventLocation getLocation() { return location; }
    public void setLocation(EventLocation location) { this.location = location; }
}


