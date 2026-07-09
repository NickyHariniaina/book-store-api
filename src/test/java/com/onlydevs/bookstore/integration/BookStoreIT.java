package com.onlydevs.bookstore.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class BookStoreIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private BookStoreRepository bookStoreRepository;

  private WebTestClient webTestClient;

  private BookStoreResponse createdStore;

  @AfterEach
  void tearDown() {
    bookStoreRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    createdStore =
        webTestClient
            .post()
            .uri("/stores")
            .bodyValue(
                CreateBookStoreRequest.builder()
                    .name("Main Store")
                    .address("123 Main St")
                    .phone("0340000000")
                    .email("main@store.com")
                    .build())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BookStoreResponse.class)
            .returnResult()
            .getResponseBody();
  }

  @Test
  void create_store_should_persist_and_return_store() {
    webTestClient
        .post()
        .uri("/stores")
        .bodyValue(
            CreateBookStoreRequest.builder()
                .name("Branch Store")
                .address("456 Branch St")
                .phone("0340000001")
                .email("branch@store.com")
                .build())
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("Branch Store")
        .jsonPath("$.address")
        .isEqualTo("456 Branch St")
        .jsonPath("$.id")
        .isNotEmpty();
  }

  @Test
  void get_store_by_id_should_return_store() {
    webTestClient
        .get()
        .uri("/stores/" + createdStore.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("Main Store")
        .jsonPath("$.address")
        .isEqualTo("123 Main St");
  }

  @Test
  void get_all_stores_should_return_all_stores() {
    webTestClient
        .post()
        .uri("/stores")
        .bodyValue(
            CreateBookStoreRequest.builder()
                .name("Second Store")
                .address("789 Second St")
                .phone("0340000002")
                .email("second@store.com")
                .build())
        .exchange()
        .expectStatus()
        .isCreated();

    webTestClient
        .get()
        .uri("/stores")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.totalElements")
        .value(greaterThanOrEqualTo(2));
  }

  @Test
  void update_store_should_modify_and_return_store() {
    UpdateBookStoreRequest updateRequest =
        UpdateBookStoreRequest.builder().name("Updated Store").address("999 Updated Blvd").build();

    webTestClient
        .put()
        .uri("/stores/" + createdStore.getId())
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("Updated Store")
        .jsonPath("$.address")
        .isEqualTo("999 Updated Blvd")
        .jsonPath("$.id")
        .isEqualTo(createdStore.getId().toString());
  }

  @Test
  void delete_store_should_remove_store() {
    webTestClient
        .delete()
        .uri("/stores/" + createdStore.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    webTestClient
        .get()
        .uri("/stores/" + createdStore.getId())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void get_store_by_id_should_return_404_when_not_found() {
    webTestClient
        .get()
        .uri("/stores/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("BookStore not found"));
  }

  @Test
  void update_store_should_return_404_when_not_found() {
    webTestClient
        .put()
        .uri("/stores/" + UUID.randomUUID())
        .bodyValue(UpdateBookStoreRequest.builder().name("Ghost Store").build())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("BookStore not found"));
  }

  @Test
  void delete_store_should_return_404_when_not_found() {
    webTestClient
        .delete()
        .uri("/stores/" + UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("BookStore not found"));
  }

  @Test
  void create_store_should_return_400_when_name_missing() {
    webTestClient
        .post()
        .uri("/stores")
        .bodyValue(
            CreateBookStoreRequest.builder()
                .address("456 Branch St")
                .phone("0340000001")
                .email("branch@store.com")
                .build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("400 BAD_REQUEST")
        .jsonPath("$.message")
        .value(containsString("name"));
  }
}
