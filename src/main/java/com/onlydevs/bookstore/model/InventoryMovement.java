package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"inventory_movement\"")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class InventoryMovement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "\"inventory_movement_type\"", nullable = false)
  private InventoryMovementType inventoryMovementType;

  @Positive
  @Column(nullable = false)
  private Integer quantity;

  @NotBlank
  @Column(nullable = false)
  private String reason;

  private String reference;

  @Column(name = "\"moved_at\"")
  @CreationTimestamp
  private Instant movedAt;

  @Column(name = "\"created_by\"")
  private String createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_store_id\"", nullable = false)
  private BookStore bookStore;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_edition_id\"", nullable = false)
  private BookEdition bookEdition;
}
