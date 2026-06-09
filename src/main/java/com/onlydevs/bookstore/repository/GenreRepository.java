package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Genre;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, UUID> {

  Optional<Genre> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  List<Genre> findByBooksId(UUID bookId);

  @Query(
      "SELECT g.name, COUNT(b) FROM Genre g JOIN g.books b GROUP BY g.name ORDER BY COUNT(b) DESC")
  List<Object[]> countBooksPerGenre();

  @Query(
      """
      SELECT g.name, SUM(si.unitPrice * si.quantity)
      FROM Genre g
      JOIN g.books b
      JOIN b.bookEditions e
      JOIN e.saleItems si
      JOIN si.sale s
      WHERE s.bookStore.id = :storeId
        AND s.status = 'PAID'
        AND s.createdAt BETWEEN :from AND :to
      GROUP BY g.name
      ORDER BY SUM(si.unitPrice * si.quantity) DESC
      """)
  List<Object[]> revenueByGenre(
      @Param("storeId") UUID storeId,
      @Param("from") java.time.Instant from,
      @Param("to") java.time.Instant to);
}
