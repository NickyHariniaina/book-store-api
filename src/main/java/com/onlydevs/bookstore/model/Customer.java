package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"customer\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "\"first_name\"", nullable = false)
  private String firstName;

  @Column(name = "\"last_name\"", nullable = false)
  private String lastName;

  @Column(unique = true)
  private String email;

  @Column(unique = true, nullable = true)
  private String phone;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "customer")
  private List<Sale> sales = new ArrayList<>();
}
