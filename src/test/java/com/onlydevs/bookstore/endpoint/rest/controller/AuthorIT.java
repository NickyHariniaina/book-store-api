package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import com.onlydevs.bookstore.repository.CustomerRepository;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

@RequiredArgsConstructor
class AuthorIT extends FacadeIT {

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

  private AuthorResponse createdAuthor;

  private final PasswordEncoder passwordEncoder;
  private final CustomerRepository customerRepository;

  @BeforeEach
  void setUp() {
    customerRepository.save(
        Customer.builder()
            .firstName("Admin")
            .lastName("User")
            .email("admin@bookstore.com")
            .password(passwordEncoder.encode("admin123"))
            .role("ADMIN")
            .build());

    webTestClient =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth("admin@bookstore.com", "admin123"))
            .build();
    createdAuthor =
        webTestClient
            .post()
            .uri("/api/v1/authors")
            .bodyValue(CreateAuthorRequest.builder().firstName("Jane").lastName("Austen").build())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuthorResponse.class)
            .returnResult()
            .getResponseBody();
  }

  @Test
  void createAuthor_should_persist_and_return_author() {
    var request = CreateAuthorRequest.builder().firstName("Jane").lastName("Austen").build();

    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.firstName")
        .isEqualTo("Jane")
        .jsonPath("$.lastName")
        .isEqualTo("Austen")
        .jsonPath("$.fullName")
        .isEqualTo("Jane Austen")
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.createdAt")
        .isNotEmpty();
  }

  @Test
  void findById_should_return_author() {
    webTestClient
        .get()
        .uri("/api/v1/authors/" + createdAuthor.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.firstName")
        .isEqualTo("Jane")
        .jsonPath("$.lastName")
        .isEqualTo("Austen")
        .jsonPath("$.fullName")
        .isEqualTo("Jane Austen");
  }

  @Test
  void getAll_should_return_all_authors() {
    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(CreateAuthorRequest.builder().firstName("Albert").lastName("Camus").build())
        .exchange()
        .expectStatus()
        .isCreated();

    webTestClient
        .get()
        .uri("/api/v1/authors")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.totalElements")
        .value(greaterThanOrEqualTo(2));
  }

  @Test
  void update_should_modify_and_return_author() {
    var updateRequest = UpdateAuthorRequest.builder().firstName("Emily").lastName("Bronte").build();

    webTestClient
        .put()
        .uri("/api/v1/authors/" + createdAuthor.getId())
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.firstName")
        .isEqualTo("Emily")
        .jsonPath("$.lastName")
        .isEqualTo("Bronte")
        .jsonPath("$.id")
        .isEqualTo(createdAuthor.getId().toString());
  }

  @Test
  void delete_should_remove_author() {
    webTestClient
        .delete()
        .uri("/api/v1/authors/" + createdAuthor.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    webTestClient
        .get()
        .uri("/api/v1/authors/" + createdAuthor.getId())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void getById_should_return_404_when_not_found() {
    webTestClient
        .get()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("doesn't exist"));
  }

  @Test
  void update_should_return_404_when_not_found() {
    webTestClient
        .put()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .bodyValue(UpdateAuthorRequest.builder().firstName("Test").lastName("Test").build())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("doesn't exist"));
  }

  @Test
  void delete_should_return_404_when_not_found() {
    webTestClient
        .delete()
        .uri("/api/v1/authors/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("doesn't exist"));
  }

  @Test
  void createAuthor_should_return_400_when_firstName_missing() {
    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(CreateAuthorRequest.builder().lastName("Austen").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("400 BAD_REQUEST")
        .jsonPath("$.message")
        .value(containsString("firstName"));
  }

  @Test
  void createAuthor_should_return_400_when_lastName_missing() {
    webTestClient
        .post()
        .uri("/api/v1/authors")
        .bodyValue(CreateAuthorRequest.builder().firstName("Jane").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("400 BAD_REQUEST")
        .jsonPath("$.message")
        .value(containsString("lastName"));
  }

  @Test
  void update_should_do_nothing_when_body_empty() {
    webTestClient
        .put()
        .uri("/api/v1/authors/" + createdAuthor.getId())
        .bodyValue(UpdateAuthorRequest.builder().build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.firstName")
        .isEqualTo("Jane")
        .jsonPath("$.lastName")
        .isEqualTo("Austen");
  }

  private String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }
}
