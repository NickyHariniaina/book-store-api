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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "\"book_store\"")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class BookStore {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  @Column(unique = true, nullable = false)
  private String name;

  @Size(max = 500, message = "address must not exceed 500 characters")
  private String address;

  @NotBlank
  @Size(max = 20, message = "phone must not exceed 20 characters")
  @Column(unique = true)
  private String phone;

  @Email
  @Size(max = 255, message = "email must not exceed 255 characters")
  @Column(unique = true)
  private String email;

  @CreationTimestamp
  @Column(name = "\"created_at\"")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "\"updated_at\"")
  private Instant updatedAt;

  @Builder.Default
  @OneToMany(
      mappedBy = "bookStore",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<Sale> sales = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "bookStore", cascade = CascadeType.PERSIST)
  private List<InventoryMovement> inventoryMovements = new ArrayList<>();
}
