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
@Table(name = "\"customer\"")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Column(name = "\"first_name\"", nullable = false)
  private String firstName;

  @NotBlank
  @Column(name = "\"last_name\"", nullable = false)
  private String lastName;

  @Email
  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String phone;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "customer")
  private List<Sale> sales = new ArrayList<>();
}
