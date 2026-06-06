package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
  @Column(name = "\"payment_method\"")
  private PaymentMethod paymentMethod;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_store_id\"", nullable = false)
  private BookStore bookStore;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"customer_id\"", nullable = false)
  private Customer customer;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
  private List<SaleItem> saleItems = new ArrayList<>();
}
