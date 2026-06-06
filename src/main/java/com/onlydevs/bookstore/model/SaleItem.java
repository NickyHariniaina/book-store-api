package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"sale_item\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Positive
  @Column(nullable = false)
  private Integer quantity;

  @DecimalMin("0.00")
  @Column(name = "\"unit_price\"", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @DecimalMin("0.00")
  @Column(name = "\"discount_percent\"", precision = 5, scale = 2)
  private BigDecimal discountPercent;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"sale_id\"", nullable = false)
  private Sale sale;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_edition_id\"", nullable = false)
  private BookEdition bookEdition;
}
