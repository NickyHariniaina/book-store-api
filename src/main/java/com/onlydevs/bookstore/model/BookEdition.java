package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.BookFormat;
import java.util.List;

import jakarta.persistence.*;
import java.time.Instant;
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
public class BookEdition {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String isbn;

  private String edition;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookFormat format;

  private Boolean active = true;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "publisher_id")
  private Publisher publisher;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL)
  private List<InventoryItem> InventoryItems;
}
