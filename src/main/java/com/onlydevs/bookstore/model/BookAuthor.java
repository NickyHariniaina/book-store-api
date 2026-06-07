package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "\"book_author\"",
    uniqueConstraints = @UniqueConstraint(columnNames = {"\"book_id\"", "\"author_id\""}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class BookAuthor {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_id\"", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"author_id\"", nullable = false)
  private Author author;
}
