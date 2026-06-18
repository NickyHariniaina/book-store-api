package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
  @NotNull
  @Size(min = 1, max = 100, message = "first name must be between 1 and 100 characters")
  @Column(name = "\"first_name\"", nullable = false)
  private String firstName;

  @NotBlank
  @NotNull
  @Size(min = 1, max = 100, message = "last name must be between 1 and 100 characters")
  @Column(name = "\"last_name\"", nullable = false)
  private String lastName;

  @Email
  @Size(max = 255, message = "email must not exceed 255 characters")
  @Column(unique = true)
  private String email;

  @Size(max = 20, message = "phone must not exceed 20 characters")
  @Column(unique = true)
  private String phone;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @Builder.Default
  @OneToMany(mappedBy = "customer")
  private List<Sale> sales = new ArrayList<>();
}
