package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Genre;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GenreRepository extends JpaRepository<Genre, UUID> {

  boolean existsByNameIgnoreCase(String name);

  @Query(
      """
      SELECT g.name, SUM(si.unitPrice * si.quantity)
      FROM Genre g
      JOIN g.books b
      JOIN b.bookEditions e
      JOIN e.saleItems si
      JOIN si.sale s
      WHERE s.status = 'PAID'
      GROUP BY g.name
      ORDER BY SUM(si.unitPrice * si.quantity) DESC
      """)
  List<Object[]> revenueByGenre();
}
