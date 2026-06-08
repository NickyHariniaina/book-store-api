package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.AuthorRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"book_id\"", nullable = false)
  private Book book;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"author_id\"", nullable = false)
  private Author author;

  @Enumerated(EnumType.STRING)
  @Column
  private AuthorRole role;

  @Column(name = "\"contribution_order\"")
  private Integer contributionOrder;
}
