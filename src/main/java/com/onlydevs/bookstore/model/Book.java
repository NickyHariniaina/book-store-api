package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.BookLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Column(nullable = false)
  private String title;

  @Column private String summary;

  @Enumerated(EnumType.STRING)
  @Column
  private BookLanguage language;

  @Column(name = "\"cover_url\"")
  private String coverUrl;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @ManyToMany
  @JoinTable(
      name = "\"book_genre\"",
      joinColumns = @JoinColumn(name = "\"book_id\""),
      inverseJoinColumns = @JoinColumn(name = "\"genre_id\""))
  @Builder.Default
  private Set<Genre> genres = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookAuthor> bookAuthors = new ArrayList<>();
}
