package com.onlydevs.bookstore.model;

import com.onlydevs.bookstore.model.enums.BookFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name= "\"book_edition\"")
@NoArgsConstructor
public class BookEdition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)
    private String isbn;

    private Publisher publisher;

    private String edition;

    @Enumerated(EnumType.STRING)
    private BookFormat format;

    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "\"created_at\"")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "\"updated_at\"")
    private Instant updatedAt;
}
