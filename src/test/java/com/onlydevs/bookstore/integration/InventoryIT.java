package com.onlydevs.bookstore.integration;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.dto.request.ReorderLevelRequest;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class InventoryIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private BookStoreRepository bookStoreRepository;

  @Autowired private BookEditionRepository bookEditionRepository;

  @Autowired private InventoryItemRepository inventoryItemRepository;

  private WebTestClient webTestClient;

  private UUID storeId;

  private UUID editionId;

  @AfterEach
  void tearDown() {
    inventoryItemRepository.deleteAll();
    bookEditionRepository.deleteAll();
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

    var edition =
        bookEditionRepository.save(
            BookEdition.builder().isbn("9780000000001").format(BookFormat.PAPERBACK).build());
    editionId = edition.getId();
  }

  @Test
  void get_stock_by_edition_should_return_404_when_not_found() {
    webTestClient
        .get()
        .uri("/stores/{storeId}/stock/{editionId}", storeId, UUID.randomUUID())
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
        .uri("/stores/{storeId}/movements", storeId)
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
        .uri("/stores/{storeId}/movements?type=INVALID", storeId)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.type")
        .isEqualTo("400 BAD_REQUEST")
        .jsonPath("$.message")
        .value(containsString("Unknown inventory movement type"));
  }

  @Test
  void update_reorder_level_should_modify_and_return_item() {
    var store = bookStoreRepository.findById(storeId).orElseThrow();
    var edition = bookEditionRepository.findById(editionId).orElseThrow();
    var item =
        inventoryItemRepository.save(
            InventoryItem.builder()
                .bookStore(store)
                .bookEdition(edition)
                .quantityOnHand(5)
                .reorderLevel(3)
                .build());

    var request = new ReorderLevelRequest();
    request.setReorderLevel(10);

    webTestClient
        .patch()
        .uri("/stores/{storeId}/inventory/{editionId}/reorder-level", storeId, editionId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.reorderLevel")
        .isEqualTo(10);

    var updated = inventoryItemRepository.findById(item.getId()).orElseThrow();
    assertEquals(10, updated.getReorderLevel());
  }
}
