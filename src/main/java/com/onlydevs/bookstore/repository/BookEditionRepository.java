package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.BookEdition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEditionRepository extends JpaRepository<BookEdition, UUID> {
  List<BookEdition> findByBookId(UUID bookId);

  Optional<BookEdition> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);
}
