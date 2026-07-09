package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.*;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.repository.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

class GenreIT extends FacadeIT {

  private WebTestClient webTestClient;

  @LocalServerPort int port;

  @Autowired private GenreRepository genreRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private BookEditionRepository bookEditionRepository;
  @Autowired private SaleRepository saleRepository;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    saleRepository.deleteAll();
    bookEditionRepository.deleteAll();
    bookRepository.deleteAll();
    genreRepository.deleteAll();
  }

  @Test
  void should_create_genre_ok() {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();

    webTestClient
        .post()
        .uri("/genres")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(GenreResponse.class)
        .value(
            response -> {
              assertNotNull(response.getId());
              assertEquals("Fiction", response.getName());
            });
  }

  @Test
  void should_fail_when_duplicate_name() {
    genreRepository.save(Genre.builder().name("Fiction").build());

    var request = CreateGenreRequest.builder().name("Fiction").build();

    webTestClient
        .post()
        .uri("/genres")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_get_all_genres_ok() {
    genreRepository.save(Genre.builder().name("Science").build());
    genreRepository.save(Genre.builder().name("Fiction").build());

    webTestClient
        .get()
        .uri("/genres?page=0&size=10")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content.length()")
        .isEqualTo(2)
        .jsonPath("$.totalElements")
        .isEqualTo(2);
  }

  @Test
  void should_rename_genre_ok() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    var request = RenameGenreRequest.builder().name("Science").build();

    webTestClient
        .patch()
        .uri("/genres/{id}/rename", saved.getId())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(GenreResponse.class)
        .value(response -> assertEquals("Science", response.getName()));
  }

  @Test
  void should_fail_when_rename_not_found() {
    var request = RenameGenreRequest.builder().name("Science").build();

    webTestClient
        .patch()
        .uri("/genres/{id}/rename", UUID.randomUUID())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_fail_when_rename_duplicate() {
    genreRepository.save(Genre.builder().name("Science").build());
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    var request = RenameGenreRequest.builder().name("Science").build();

    webTestClient
        .patch()
        .uri("/genres/{id}/rename", saved.getId())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_delete_genre_ok() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    webTestClient
        .delete()
        .uri("/genres/{id}", saved.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    assertFalse(genreRepository.existsById(saved.getId()));
  }

  @Test
  void should_fail_when_delete_not_found() {
    webTestClient
        .delete()
        .uri("/genres/{id}", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_get_books_by_genre_ok_when_empty() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    webTestClient.get().uri("/genres/{id}/books", saved.getId()).exchange().expectStatus().isOk();
  }

  @Test
  void should_get_books_by_genre_fail_when_not_found() {
    webTestClient
        .get()
        .uri("/genres/{id}/books", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  private record TestData(
      Genre fiction, Genre science, BookEdition editionA, BookEdition editionB) {

    static TestData create(
        GenreRepository genreRepository,
        BookRepository bookRepository,
        BookEditionRepository bookEditionRepository) {
      var fiction = genreRepository.save(Genre.builder().name("Fiction").build());
      var science = genreRepository.save(Genre.builder().name("Science").build());
      var bookA =
          bookRepository.save(
              Book.builder()
                  .title("Fiction Book")
                  .language(BookLanguage.ENGLISH)
                  .genres(Set.of(fiction))
                  .build());
      var bookB =
          bookRepository.save(
              Book.builder()
                  .title("Science Book")
                  .language(BookLanguage.ENGLISH)
                  .genres(Set.of(science))
                  .build());
      var editionA =
          bookEditionRepository.save(
              BookEdition.builder()
                  .isbn("9780000000001")
                  .format(BookFormat.PAPERBACK)
                  .book(bookA)
                  .build());
      var editionB =
          bookEditionRepository.save(
              BookEdition.builder()
                  .isbn("9780000000002")
                  .format(BookFormat.PAPERBACK)
                  .book(bookB)
                  .build());
      return new TestData(fiction, science, editionA, editionB);
    }
  }

  private static Sale paidSale(BookEdition edition, int quantity, double price) {
    var sale = Sale.builder().status(SaleStatus.PAID).build();
    sale.setSaleItems(
        List.of(
            SaleItem.builder()
                .bookEdition(edition)
                .quantity(quantity)
                .unitPrice(BigDecimal.valueOf(price))
                .sale(sale)
                .build()));
    return sale;
  }

  @Test
  void should_get_revenue_per_genre_with_paid_sales_only() {
    var data = TestData.create(genreRepository, bookRepository, bookEditionRepository);
    saleRepository.save(paidSale(data.editionA, 2, 10.00));
    saleRepository.save(paidSale(data.editionB, 3, 15.00));

    webTestClient
        .get()
        .uri("/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(2)
        .consumeWith(
            result -> {
              var revenues = result.getResponseBody();
              assertNotNull(revenues);
              var fictionRev =
                  revenues.stream()
                      .filter(r -> r.getGenreName().equals("Fiction"))
                      .findFirst()
                      .orElseThrow();
              var scienceRev =
                  revenues.stream()
                      .filter(r -> r.getGenreName().equals("Science"))
                      .findFirst()
                      .orElseThrow();
              assertEquals(0, BigDecimal.valueOf(20.00).compareTo(fictionRev.getRevenue()));
              assertEquals(0, BigDecimal.valueOf(45.00).compareTo(scienceRev.getRevenue()));
            });
  }

  @Test
  void should_get_revenue_per_genre_empty_when_no_sales() {
    TestData.create(genreRepository, bookRepository, bookEditionRepository);

    webTestClient
        .get()
        .uri("/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(0);
  }

  @Test
  void should_get_revenue_per_genre_empty_when_only_pending_sales() {
    var data = TestData.create(genreRepository, bookRepository, bookEditionRepository);
    var pendingSale = Sale.builder().status(SaleStatus.PENDING).build();
    pendingSale.setSaleItems(
        List.of(
            SaleItem.builder()
                .bookEdition(data.editionA)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(100.00))
                .sale(pendingSale)
                .build()));
    saleRepository.save(pendingSale);

    webTestClient
        .get()
        .uri("/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(0);
  }

  @Test
  void should_get_revenue_per_genre_aggregates_multiple_sales() {
    var data = TestData.create(genreRepository, bookRepository, bookEditionRepository);
    saleRepository.save(paidSale(data.editionA, 1, 5.00));
    saleRepository.save(paidSale(data.editionA, 3, 5.00));

    webTestClient
        .get()
        .uri("/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(1)
        .consumeWith(
            result -> {
              var revenues = result.getResponseBody();
              assertNotNull(revenues);
              var fictionRev =
                  revenues.stream()
                      .filter(r -> r.getGenreName().equals("Fiction"))
                      .findFirst()
                      .orElseThrow();
              assertEquals(0, BigDecimal.valueOf(20.00).compareTo(fictionRev.getRevenue()));
            });
  }

  @Test
  void should_fail_when_invalid_uuid_for_get_books() {
    webTestClient
        .get()
        .uri("/genres/{id}/books", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_rename() {
    var request = RenameGenreRequest.builder().name("Science").build();

    webTestClient
        .patch()
        .uri("/genres/{id}/rename", "invalid-uuid")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_delete() {
    webTestClient
        .delete()
        .uri("/genres/{id}", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
