package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class AuthorIT extends FacadeIT {

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    webTestClient =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();
  }

  @Test
  void createAuthor_should_persist_and_return_author() {
    var request = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var result =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void findById_should_return_author() {
    var createRequest = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var created =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(createRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    var result =
        webTestClient
            .get()
            .uri("/api/v1/authors/" + created.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    assertEquals(created.getId(), result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
  }

  @Test
  void getAll_should_return_all_authors() {
    var author1 =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(new CreateAuthorRequest().firstName("Jane").lastName("Austen"))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();
    var author2 =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(new CreateAuthorRequest().firstName("Albert").lastName("Camus"))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    var result =
        webTestClient
            .get()
            .uri("/api/v1/authors")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    assertTrue(result.stream().anyMatch(a -> a.getId().equals(author1.getId())));
    assertTrue(result.stream().anyMatch(a -> a.getId().equals(author2.getId())));
  }

  @Test
  void update_should_modify_and_return_author() {
    var createRequest = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var created =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(createRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    var updateRequest = new UpdateAuthorRequest().firstName("Emily").lastName("Bronte");

    var updated =
        webTestClient
            .put()
            .uri("/api/v1/authors/" + created.getId())
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    assertEquals(created.getId(), updated.getId());
    assertEquals("Emily", updated.getFirstName());
    assertEquals("Bronte", updated.getLastName());
  }

  @Test
  void delete_should_remove_author() {
    var createRequest = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var created =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(createRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    webTestClient
        .delete()
        .uri("/api/v1/authors/" + created.getId())
        .exchange()
        .expectStatus()
        .isOk();

    webTestClient
        .get()
        .uri("/api/v1/authors/" + created.getId())
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
