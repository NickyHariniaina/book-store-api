package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
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

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "\"inventory_movement_type\"", nullable = false)
  private InventoryMovementType inventoryMovementType;

  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer quantity;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 500, message = "reason must be between 1 and 500 characters")
  @Column(nullable = false)
  private String reason;

  @Size(max = 255, message = "reference must not exceed 255 characters")
  private String reference;

  @Column(name = "\"moved_at\"")
  @CreationTimestamp
  private Instant movedAt;

  @Size(max = 100, message = "created by must not exceed 100 characters")
  @Column(name = "\"created_by\"")
  private String createdBy;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_edition_id\"", nullable = false)
  private BookEdition bookEdition;
}
