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
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.GenreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
  @Autowired private BookStoreRepository bookStoreRepository;
  @PersistenceContext private EntityManager entityManager;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    genreRepository.deleteAll();
    bookEditionRepository.deleteAll();
    bookRepository.deleteAll();
    bookStoreRepository.deleteAll();
  }

  @Test
  void should_create_genre_ok() {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();

    webTestClient
        .post()
        .uri("/api/v1/genres")
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
        .uri("/api/v1/genres")
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
        .uri("/api/v1/genres?page=0&size=10")
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
        .uri("/api/v1/genres/{id}/rename", saved.getId())
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
        .uri("/api/v1/genres/{id}/rename", UUID.randomUUID())
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
        .uri("/api/v1/genres/{id}/rename", saved.getId())
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
        .uri("/api/v1/genres/{id}", saved.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    assertFalse(genreRepository.existsById(saved.getId()));
  }

  @Test
  void should_fail_when_delete_not_found() {
    webTestClient
        .delete()
        .uri("/api/v1/genres/{id}", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_get_books_by_genre_ok_when_empty() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    webTestClient
        .get()
        .uri("/api/v1/genres/{id}/books", saved.getId())
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void should_get_books_by_genre_fail_when_not_found() {
    webTestClient
        .get()
        .uri("/api/v1/genres/{id}/books", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_get_revenue_per_genre_ok() {
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

    var store =
        bookStoreRepository.save(
            BookStore.builder()
                .name("Main Store")
                .phone("+261000000001")
                .email("store@test.com")
                .build());

    var sale = Sale.builder().status(SaleStatus.PAID).bookStore(store).build();
    var itemA =
        SaleItem.builder()
            .bookEdition(editionA)
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(10.00))
            .sale(sale)
            .build();
    var itemB =
        SaleItem.builder()
            .bookEdition(editionB)
            .quantity(3)
            .unitPrice(BigDecimal.valueOf(15.00))
            .sale(sale)
            .build();
    sale.setSaleItems(List.of(itemA, itemB));
    entityManager.persist(sale);

    webTestClient
        .get()
        .uri("/api/v1/genres/revenue")
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
              assertEquals(
                  BigDecimal.valueOf(20.00).setScale(2), fictionRev.getRevenue().setScale(2));
              assertEquals(
                  BigDecimal.valueOf(45.00).setScale(2), scienceRev.getRevenue().setScale(2));
            });
  }

  @Test
  void should_get_revenue_per_genre_when_no_sales() {
    genreRepository.save(Genre.builder().name("Fiction").build());

    webTestClient
        .get()
        .uri("/api/v1/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(1)
        .consumeWith(
            result -> {
              var revenues = result.getResponseBody();
              assertNotNull(revenues);
              assertEquals("Fiction", revenues.getFirst().getGenreName());
              assertEquals(BigDecimal.ZERO.setScale(2), revenues.getFirst().getRevenue());
            });
  }

  @Test
  void should_get_revenue_per_genre_exclude_pending_sales() {
    var fiction = genreRepository.save(Genre.builder().name("Fiction").build());

    var book =
        bookRepository.save(
            Book.builder()
                .title("Fiction Book")
                .language(BookLanguage.ENGLISH)
                .genres(Set.of(fiction))
                .build());

    var edition =
        bookEditionRepository.save(
            BookEdition.builder()
                .isbn("9780000000003")
                .format(BookFormat.PAPERBACK)
                .book(book)
                .build());

    var store =
        bookStoreRepository.save(
            BookStore.builder()
                .name("Main Store")
                .phone("+261000000002")
                .email("store2@test.com")
                .build());

    var pendingSale = Sale.builder().status(SaleStatus.PENDING).bookStore(store).build();
    var pendingItem =
        SaleItem.builder()
            .bookEdition(edition)
            .quantity(5)
            .unitPrice(BigDecimal.valueOf(100.00))
            .sale(pendingSale)
            .build();
    pendingSale.setSaleItems(List.of(pendingItem));
    entityManager.persist(pendingSale);

    webTestClient
        .get()
        .uri("/api/v1/genres/revenue")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(RevenuePerGenreResponse.class)
        .hasSize(1)
        .consumeWith(
            result -> {
              var revenues = result.getResponseBody();
              assertNotNull(revenues);
              assertEquals("Fiction", revenues.getFirst().getGenreName());
              assertEquals(BigDecimal.ZERO.setScale(2), revenues.getFirst().getRevenue());
            });
  }

  @Test
  void should_fail_when_invalid_uuid_for_get_books() {
    webTestClient
        .get()
        .uri("/api/v1/genres/{id}/books", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_rename() {
    var request = RenameGenreRequest.builder().name("Science").build();

    webTestClient
        .patch()
        .uri("/api/v1/genres/{id}/rename", "invalid-uuid")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_delete() {
    webTestClient
        .delete()
        .uri("/api/v1/genres/{id}", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
