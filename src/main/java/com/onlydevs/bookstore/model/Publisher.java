package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"publisher\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String phone;

  @Column private String email;

  @Column private String country;

  @Column private String website;

  @Column(name = "\"created_at\"")
  @CreationTimestamp
  private Instant createdAt;

  @OneToMany(mappedBy = "publisher")
  private List<BookEdition> bookEditions = new ArrayList<>();
}
