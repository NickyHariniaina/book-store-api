package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.BookPriceHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPriceHistoryRepository extends JpaRepository<BookPriceHistory, UUID> {
  List<BookPriceHistory> findByBookEditionIdOrderByEffectiveFromDesc(UUID bookEditionId);

  Optional<BookPriceHistory> findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(
      UUID bookEditionId);

  Optional<BookPriceHistory> findFirstByBookEditionIdOrderByEffectiveFromDesc(UUID bookEditionId);
}
