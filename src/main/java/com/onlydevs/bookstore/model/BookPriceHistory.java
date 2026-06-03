package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "\"book_price_history\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "\"effective_from\"", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "\"effective_to\"")
    private Instant effectiveTo;

    @CreationTimestamp
    @Column(name = "\"created_at\"")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"book_edition_id\"", nullable = false)
    private BookEdition bookEdition;
}
