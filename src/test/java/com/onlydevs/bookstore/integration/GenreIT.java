package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.repository.GenreRepository;
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

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    genreRepository.deleteAll();
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
        .uri("/api/v1/genres")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(
            body -> {
              assertNotNull(body);
              assertTrue(body.contains("Fiction"));
              assertTrue(body.contains("Science"));
            });
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
    webTestClient.get().uri("/api/v1/genres/revenue").exchange().expectStatus().isOk();
  }
}
