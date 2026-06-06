package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Column(nullable = false)
  private String phone;

  @Email @Column private String email;

  @Column private String country;

  @Column private String website;

  @Column(name = "\"created_at\"")
  @CreationTimestamp
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "publisher")
  private List<BookEdition> bookEditions = new ArrayList<>();
}
