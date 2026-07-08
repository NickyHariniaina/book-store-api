package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "\"genre\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Genre {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100, message = "name must be between 1 and 100 characters")
  @Column(nullable = false, unique = true)
  private String name;

  @Size(max = 1000, message = "description must not exceed 1000 characters")
  @Column
  private String description;

  @ManyToMany(mappedBy = "genres")
  @Builder.Default
  private Set<Book> books = new HashSet<>();

  public void rename(String newName) {
    if (newName == null || newName.isBlank()) {
      throw new IllegalArgumentException("Genre name cannot be blank");
    }

    this.name = newName.trim();
  }
}
