package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.BookFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "\"book_edition\"")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class BookEdition {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(min = 10, max = 13, message = "isbn must be between 10 and 13 characters")
  @Column(unique = true, nullable = false)
  private String isbn;

  @Size(max = 50, message = "edition must not exceed 50 characters")
  private String edition;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookFormat format;

  @Builder.Default private Boolean active = true;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"publisher_id\"")
  private Publisher publisher;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_id\"", nullable = false)
  private Book book;

  @Builder.Default
  @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL)
  private List<InventoryItem> inventoryItems = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL)
  private List<InventoryMovement> inventoryMovements = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL)
  private List<BookPriceHistory> priceHistory = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL)
  private List<SaleItem> saleItems = new ArrayList<>();
}
