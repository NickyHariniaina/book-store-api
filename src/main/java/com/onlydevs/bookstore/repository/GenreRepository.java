package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, UUID> {

  interface GenreRevenue {
    String getName();

    BigDecimal getRevenue();
  }

  boolean existsByNameIgnoreCase(String name);

  @Query("SELECT b FROM Genre g JOIN g.books b WHERE g.id = :genreId")
  Page<Book> findBooksByGenreId(@Param("genreId") UUID genreId, Pageable pageable);

  @Query(
      """
      SELECT g.name AS name, SUM(si.unitPrice * si.quantity) AS revenue
      FROM Genre g
      JOIN g.books b
      JOIN b.bookEditions e
      JOIN e.saleItems si
      JOIN si.sale s
      WHERE s.status = 'PAID'
      GROUP BY g.name
      ORDER BY SUM(si.unitPrice * si.quantity) DESC
      """)
  List<GenreRevenue> revenueByGenre();
}
