package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "\"unit_price\"", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

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
