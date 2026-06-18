package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class AuthorIT extends FacadeIT {

  private WebTestClient webTestClient;

  @LocalServerPort int port;

  @Autowired private AuthorRepository authorRepository;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    authorRepository.deleteAll();
  }

  @Test
  void should_create_author_ok() {
    var request = CreateAuthorRequest.builder().firstName("Jane").lastName("Austen").build();

    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(AuthorResponse.class)
        .value(
            response -> {
              assertNotNull(response.getId());
              assertEquals("Jane", response.getFirstName());
              assertEquals("Austen", response.getLastName());
              assertEquals("Jane Austen", response.getFullName());
              assertNotNull(response.getCreatedAt());
            });
  }

  @Test
  void should_fail_when_creating_author_without_firstName() {
    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(CreateAuthorRequest.builder().lastName("Austen").build())
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_creating_author_without_lastName() {
    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(CreateAuthorRequest.builder().firstName("Jane").build())
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_get_author_by_id_ok() {
    var saved =
        authorRepository.save(Author.builder().firstName("Jane").lastName("Austen").build());

    webTestClient
        .get()
        .uri("/api/v1/authors/" + saved.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(AuthorResponse.class)
        .value(
            response -> {
              assertEquals("Jane", response.getFirstName());
              assertEquals("Austen", response.getLastName());
              assertEquals("Jane Austen", response.getFullName());
            });
  }

  @Test
  void should_get_author_by_id_fail_when_not_found() {
    webTestClient
        .get()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_get_all_authors_ok() {
    authorRepository.save(Author.builder().firstName("Jane").lastName("Austen").build());
    authorRepository.save(Author.builder().firstName("Albert").lastName("Camus").build());

    webTestClient
        .get()
        .uri("/api/v1/authors")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.totalElements")
        .isEqualTo(2);
  }

  @Test
  void should_update_author_ok() {
    var saved =
        authorRepository.save(Author.builder().firstName("Jane").lastName("Austen").build());

    var request = UpdateAuthorRequest.builder().firstName("Emily").lastName("Bronte").build();

    webTestClient
        .put()
        .uri("/api/v1/authors/" + saved.getId())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(AuthorResponse.class)
        .value(
            response -> {
              assertEquals("Emily", response.getFirstName());
              assertEquals("Bronte", response.getLastName());
              assertEquals(saved.getId(), response.getId());
            });
  }

  @Test
  void should_update_author_fail_when_not_found() {
    var request = UpdateAuthorRequest.builder().firstName("Test").lastName("Test").build();

    webTestClient
        .put()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_update_author_do_nothing_when_empty_body() {
    var saved =
        authorRepository.save(Author.builder().firstName("Jane").lastName("Austen").build());

    webTestClient
        .put()
        .uri("/api/v1/authors/" + saved.getId())
        .bodyValue(UpdateAuthorRequest.builder().build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(AuthorResponse.class)
        .value(
            response -> {
              assertEquals("Jane", response.getFirstName());
              assertEquals("Austen", response.getLastName());
            });
  }

  @Test
  void should_delete_author_ok() {
    var saved =
        authorRepository.save(Author.builder().firstName("Jane").lastName("Austen").build());

    webTestClient
        .delete()
        .uri("/api/v1/authors/" + saved.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    assertFalse(authorRepository.existsById(saved.getId()));
  }

  @Test
  void should_delete_author_fail_when_not_found() {
    webTestClient
        .delete()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
