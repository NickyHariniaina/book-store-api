package com.onlydevs.bookstore.integration;

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
  void get_stock_by_edition_should_return_zero_when_not_found() {
    webTestClient
        .get()
        .uri("/inventory/editions/{editionId}/stock", java.util.UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Integer.class)
        .isEqualTo(0);
  }
}
