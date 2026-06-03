package com.onlydevs.bookstore.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "\"inventory_item\"")
@Getter
@Setter
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "\"quantity_on_hand\"", nullable = false)
    private Integer quantityOnHand;

    @Column(name="\"reorder_level\"", nullable = false)
    private Integer reorderLevel;

    @CreationTimestamp
    @Column(name="\"created_at\"", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name="\"updated_at\"")
    private Instant updatedAt;
}
