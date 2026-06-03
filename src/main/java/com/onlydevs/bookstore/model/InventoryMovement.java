package com.onlydevs.bookstore.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.onlydevs.bookstore.model.enums.InventoryMovementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "\"inventory_movement\"")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name="\"inventory_movement_type\"", nullable = false)
    private InventoryMovementType inventoryMovementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String reason;

    private String reference;

    @Column(name="\"moved_at\"")
    @CreationTimestamp
    private Instant movedAt;

    @Column(name="\"created_by\"")
    private String createdBy;

    // TODO: Change it later
    private String bookStore;

    private String bookEdition;

}
