package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"customer\"")
@Getter
@Setter
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

    @OneToMany(mappedBy = "customer")
    private List<Sale> sales = new ArrayList<>();
}
