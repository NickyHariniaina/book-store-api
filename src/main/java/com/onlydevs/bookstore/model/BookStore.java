package com.onlydevs.bookstore.model;

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
@Table(name = "\"book_store\"")
@NoArgsConstructor
public class BookStore {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String name;

  private String address;

  @Column(unique = true)
  private String phone;

  @Column(unique = true)
  private String email;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;
}
