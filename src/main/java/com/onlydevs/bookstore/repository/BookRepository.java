package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
  @EntityGraph(attributePaths = {"genres", "bookAuthors.author"})
  Page<Book> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"genres", "bookAuthors.author"})
  Optional<Book> findById(UUID id);
}
