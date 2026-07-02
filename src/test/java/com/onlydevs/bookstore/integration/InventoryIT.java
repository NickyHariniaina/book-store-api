package com.onlydevs.bookstore.integration;

import static org.hamcrest.Matchers.containsString;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class InventoryIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private BookStoreRepository bookStoreRepository;

  private WebTestClient webTestClient;

  private UUID storeId;

  @AfterEach
  void tearDown() {
    bookStoreRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

    // Create a store for inventory tests via repository
    BookStore store =
        bookStoreRepository.save(
            BookStore.builder()
                .name("Inventory Test Store")
                .address("100 Inventory Ln")
                .phone("0340000100")
                .email("inventory@test.com")
                .build());
    storeId = store.getId();
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
