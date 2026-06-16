package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

class AuthorIT extends FacadeIT {

  @Autowired private WebTestClient webTestClient;

  @Test
  void createAuthor_should_persist_and_return_author() {
    var request = new CreateAuthorRequest("Jane", "Austen");

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

    assertNotNull(result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void findById_should_return_author() {
    var createRequest = new CreateAuthorRequest("Jane", "Austen");

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
}
