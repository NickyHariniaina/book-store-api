package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "\"inventory_item\"")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @PositiveOrZero
  @Column(name = "\"quantity_on_hand\"", nullable = false)
  private Integer quantityOnHand;

  @PositiveOrZero
  @Column(name = "\"reorder_level\"", nullable = false)
  private Integer reorderLevel;

  @CreationTimestamp
  @Column(name = "\"created_at\"", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @Version
  private Long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_store_id\"", nullable = false)
  private BookStore bookStore;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_edition_id\"", nullable = false)
  private BookEdition bookEdition;
}
