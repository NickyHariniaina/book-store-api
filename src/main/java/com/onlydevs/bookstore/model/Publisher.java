package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"publisher\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Publisher {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Size(max = 20, message = "phone must not exceed 20 characters")
  @Column(nullable = false)
  private String phone;

  @Email
  @Size(max = 255, message = "email must not exceed 255 characters")
  @Column
  private String email;

  @Size(max = 100, message = "country must not exceed 100 characters")
  @Column
  private String country;

  @Column
  private String website;

  @Column(name = "\"created_at\"")
  @CreationTimestamp
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "publisher")
  private List<BookEdition> bookEditions = new ArrayList<>();
}
