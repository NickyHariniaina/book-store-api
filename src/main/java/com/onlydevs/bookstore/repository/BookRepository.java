package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    @EntityGraph(attributePaths = {"genres","bookAuthors.author"})
    Page<Book> findAll(Pageable pageable);
}
