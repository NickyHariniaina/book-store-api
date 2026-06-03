package com.onlydevs.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name= "\"isbn\"",unique = true,nullable = false)
    private String isbn;

}
