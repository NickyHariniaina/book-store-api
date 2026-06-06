package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
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
@NoArgsConstructor
@Table(name = "\"book\"")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  private String summary;

  private String language;

  @Column(name = "\"cover_url\"")
  private String coverUrl;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
  private List<BookGenre> bookGenres = new ArrayList<>();

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookAuthor> bookAuthors = new ArrayList<>();
}
