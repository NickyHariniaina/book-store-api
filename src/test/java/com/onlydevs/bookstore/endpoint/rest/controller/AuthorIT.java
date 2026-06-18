package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.RestErrorResponse;
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
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
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

  @Test
  void getAll_should_return_empty_list_when_no_authors() {
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

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getById_should_return_404_when_not_found() {
    var result =
        webTestClient
            .get()
            .uri("/api/v1/authors/" + UUID.randomUUID())
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(RestErrorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertEquals("404 NOT_FOUND", result.getType());
    assertTrue(result.getMessage().contains("doesn't exist"));
  }

  @Test
  void update_should_return_404_when_not_found() {
    var result =
        webTestClient
            .put()
            .uri("/api/v1/authors/" + UUID.randomUUID())
            .bodyValue(new UpdateAuthorRequest().firstName("Test").lastName("Test"))
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(RestErrorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertEquals("404 NOT_FOUND", result.getType());
    assertTrue(result.getMessage().contains("doesn't exist"));
  }

  @Test
  void delete_should_return_404_when_not_found() {
    var result =
        webTestClient
            .delete()
            .uri("/api/v1/authors/" + UUID.randomUUID())
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(RestErrorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertEquals("404 NOT_FOUND", result.getType());
    assertTrue(result.getMessage().contains("doesn't exist"));
  }

  @Test
  void createAuthor_should_return_400_when_firstName_missing() {
    var result =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(new CreateAuthorRequest().lastName("Austen"))
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(RestErrorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertEquals("400 BAD_REQUEST", result.getType());
    assertTrue(result.getMessage().contains("firstName"));
  }

  @Test
  void createAuthor_should_return_400_when_lastName_missing() {
    var result =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(new CreateAuthorRequest().firstName("Jane"))
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(RestErrorResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(result);
    assertEquals("400 BAD_REQUEST", result.getType());
    assertTrue(result.getMessage().contains("lastName"));
  }

  @Test
  void update_should_do_nothing_when_body_empty() {
    var created =
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

    var updated =
        webTestClient
            .put()
            .uri("/api/v1/authors/" + created.getId())
            .bodyValue(new UpdateAuthorRequest())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();

    assertEquals(created.getFirstName(), updated.getFirstName());
    assertEquals(created.getLastName(), updated.getLastName());
  }
}
