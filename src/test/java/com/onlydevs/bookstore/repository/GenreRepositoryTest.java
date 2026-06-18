package com.onlydevs.bookstore.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.SaleItem;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class GenreRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private GenreRepository genreRepository;

  @Test
  void should_return_revenue_per_genre_when_sales_exist() {
    var genre = Genre.builder().name("Fiction").build();
    var book = Book.builder().title("Test Book").genres(Set.of(genre)).build();
    var edition =
        BookEdition.builder().isbn("9781234567890").format(BookFormat.PAPERBACK).book(book).build();
    var store = BookStore.builder().name("Main Store").phone("1234567890").build();
    var sale = Sale.builder().status(SaleStatus.PAID).bookStore(store).build();
    var saleItem =
        SaleItem.builder()
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(25.00))
            .bookEdition(edition)
            .sale(sale)
            .build();

    entityManager.persist(genre);
    entityManager.persist(book);
    entityManager.persist(edition);
    entityManager.persist(store);
    entityManager.persist(sale);
    entityManager.persist(saleItem);
    entityManager.flush();

    var result = genreRepository.revenueByGenre();

    assertEquals(1, result.size());
    var row = result.getFirst();
    assertEquals("Fiction", row[0]);
    assertEquals(50.0, ((Number) row[1]).doubleValue(), 0.001);
  }

  @Test
  void should_return_empty_when_no_paid_sales() {
    var genre = Genre.builder().name("Fiction").build();
    var book = Book.builder().title("Test Book").genres(Set.of(genre)).build();
    var edition =
        BookEdition.builder().isbn("9781234567890").format(BookFormat.PAPERBACK).book(book).build();
    var store = BookStore.builder().name("Main Store").phone("1234567890").build();
    var sale = Sale.builder().status(SaleStatus.PENDING).bookStore(store).build();
    var saleItem =
        SaleItem.builder()
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(25.00))
            .bookEdition(edition)
            .sale(sale)
            .build();

    entityManager.persist(genre);
    entityManager.persist(book);
    entityManager.persist(edition);
    entityManager.persist(store);
    entityManager.persist(sale);
    entityManager.persist(saleItem);
    entityManager.flush();

    var result = genreRepository.revenueByGenre();

    assertTrue(result.isEmpty());
  }
}
