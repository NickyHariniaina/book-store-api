package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"author\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100, message = "first name must be between 1 and 100 characters")
  @Column(nullable = false, name = "\"first_name\"")
  private String firstName;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100, message = "last name must be between 1 and 100 characters")
  @Column(nullable = false, name = "\"last_name\"")
  private String lastName;

  @Column(name = "\"created_at\"")
  @CreationTimestamp
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "author")
  private List<BookAuthor> bookAuthors = new ArrayList<>();

  public String getFullName() {
    return this.firstName + " " + this.lastName;
  }
}
