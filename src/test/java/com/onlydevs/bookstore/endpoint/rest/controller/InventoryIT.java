package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class InventoryIT extends FacadeIT {

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

  private UUID storeId;

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

    // Create a store for inventory tests
    storeId =
        webTestClient
            .post()
            .uri("/api/v1/stores")
            .bodyValue(
                CreateBookStoreRequest.builder()
                    .name("Inventory Test Store")
                    .address("100 Inventory Ln")
                    .phone("0340000100")
                    .email("inventory@test.com")
                    .build())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BookStoreResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();
  }

  @Test
  void get_inventory_should_return_list() {
    webTestClient
        .get()
        .uri("/api/v1/stores/{storeId}/inventory", storeId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray();
  }

  @Test
  void get_stock_by_edition_should_return_404_when_not_found() {
    webTestClient
        .get()
        .uri("/api/v1/stores/{storeId}/inventory/{editionId}", storeId, UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("Stock not found"));
  }

  @Test
  void get_movements_should_return_list() {
    webTestClient
        .get()
        .uri("/api/v1/stores/{storeId}/movements", storeId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray();
  }

  @Test
  void get_movements_with_invalid_type_should_return_bad_request() {
    webTestClient
        .get()
        .uri("/api/v1/stores/{storeId}/movements?type=INVALID", storeId)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("400 BAD_REQUEST")
        .jsonPath("$.message")
        .value(containsString("Unknown inventory movement type"));
  }
}
