package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.BookAuthor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, UUID> {
  Optional<BookAuthor> findByBookIdAndAuthorId(UUID bookId, UUID authorId);

  boolean existsByBookIdAndAuthorId(UUID bookId, UUID authorId);
}
