package com.onlydevs.bookstore.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;

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
@Table(name = "\"sale\"")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="\"payment_method\"")
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name="\"created_at\"")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name="\"updated_at\"")
    private Instant updatedAt;

    // TODO: Add later...
    @Column(name = "\"book_store\"")
    private String bookStore;

    private String customer;

}
