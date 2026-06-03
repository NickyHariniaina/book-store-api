package com.onlydevs.bookstore.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.awt.print.Book;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"author\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "\"first_name\"")
    private String firstName;

    @Column(nullable = false, name = "\"last_name\"")
    private String lastName;

    @Column(name = "\"created_at\"")
    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "author")
    private List<Book> bookList = new ArrayList<>();
}
