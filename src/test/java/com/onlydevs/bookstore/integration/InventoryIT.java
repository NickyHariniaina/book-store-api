package com.onlydevs.bookstore.integration;

import static org.hamcrest.Matchers.containsString;

import com.onlydevs.bookstore.conf.FacadeIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class InventoryIT extends FacadeIT {

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void get_stock_by_edition_should_return_404_when_not_found() {
    webTestClient
        .get()
        .uri("/editions/{editionId}/stock", java.util.UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("404 NOT_FOUND")
        .jsonPath("$.message")
        .value(containsString("Stock not found"));
  }
}
