package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.request.AddSaleItemRequest;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

class SaleIT extends FacadeIT {

  private WebTestClient webTestClient;

  @LocalServerPort int port;

  @Autowired private BookStoreRepository bookStoreRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private BookEditionRepository bookEditionRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private InventoryItemRepository inventoryItemRepository;

  private UUID storeId;
  private UUID editionId;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

    bookStoreRepository.deleteAll();
    inventoryItemRepository.deleteAll();
    bookEditionRepository.deleteAll();
    bookRepository.deleteAll();
    publisherRepository.deleteAll();

    BookStore store =
        bookStoreRepository.save(
            BookStore.builder().name("Test Store").address("123 Test St").build());
    storeId = store.getId();

    Publisher publisher =
        publisherRepository.save(Publisher.builder().name("Test Publisher").build());

    Book book = bookRepository.save(Book.builder().title("Test Book").build());

    BookEdition edition =
        bookEditionRepository.save(
            BookEdition.builder()
                .book(book)
                .publisher(publisher)
                .isbn("978-1234567890")
                .format("PAPERBACK")
                .active(true)
                .build());
    editionId = edition.getId();

    inventoryItemRepository.save(
        InventoryItem.builder()
            .bookStore(store)
            .bookEdition(edition)
            .quantityOnHand(10)
            .reorderLevel(3)
            .build());
  }

  @Test
  void should_create_sale_and_confirm() {
    // Create sale
    SaleResponse sale =
        webTestClient
            .post()
            .uri("/stores/" + storeId + "/sales")
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(sale);
    assertEquals("PENDING", sale.getStatus().name());
    UUID saleId = sale.getId();

    // Add item
    AddSaleItemRequest addItem =
        AddSaleItemRequest.builder().editionId(editionId).quantity(2).build();

    SaleResponse afterAdd =
        webTestClient
            .post()
            .uri("/sales/" + saleId + "/items")
            .bodyValue(addItem)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(afterAdd);
    assertEquals(1, afterAdd.getItems().size());

    // Confirm sale
    SaleResponse confirmed =
        webTestClient
            .patch()
            .uri("/sales/" + saleId + "/confirm?paymentMethod=CARD")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(confirmed);
    assertEquals("PAID", confirmed.getStatus().name());

    // Verify stock decremented
    InventoryItem item =
        inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId).orElseThrow();
    assertEquals(8, item.getQuantityOnHand());
  }

  @Test
  void should_create_cancel_sale() {
    SaleResponse sale =
        webTestClient
            .post()
            .uri("/stores/" + storeId + "/sales")
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(sale);
    UUID saleId = sale.getId();

    SaleResponse cancelled =
        webTestClient
            .patch()
            .uri("/sales/" + saleId + "/cancel")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(cancelled);
    assertEquals("CANCELLED", cancelled.getStatus().name());
  }

  @Test
  void should_get_sale_by_id() {
    SaleResponse sale =
        webTestClient
            .post()
            .uri("/stores/" + storeId + "/sales")
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SaleResponse.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(sale);
    UUID saleId = sale.getId();

    webTestClient
        .get()
        .uri("/sales/" + saleId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(SaleResponse.class)
        .value(
            response -> {
              assertEquals(saleId, response.getId());
              assertEquals("Test Store", response.getStoreName());
            });
  }

  @Test
  void should_return_404_when_sale_not_found() {
    webTestClient.get().uri("/sales/" + UUID.randomUUID()).exchange().expectStatus().isNotFound();
  }
}
