package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookStoreMapper;
import com.onlydevs.bookstore.endpoint.rest.model.BookStoreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.BookStoreService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookStoreControllerTest {

  BookStoreService service = mock(BookStoreService.class);
  BookStoreMapper mapper = mock(BookStoreMapper.class);
  BookStoreController controller = new BookStoreController(service, mapper);

  private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  private static BookStore store =
      BookStore.builder()
          .id(STORE_ID)
          .name("Test Store")
          .address("123 Test St")
          .phone("0340000000")
          .email("test@store.com")
          .build();

  private static BookStoreResponse response =
      new BookStoreResponse()
          .id(STORE_ID)
          .name("Test Store")
          .address("123 Test St")
          .phone("0340000000")
          .email("test@store.com")
          .createdAt(Instant.now())
          .updatedAt(Instant.now());

  @Test
  void getStores_returnsList() {
    when(service.findAll()).thenReturn(List.of(store));
    when(mapper.toRestList(List.of(store))).thenReturn(List.of(response));

    List<BookStoreResponse> result = controller.getStores();

    assertEquals(1, result.size());
    assertEquals("Test Store", result.get(0).getName());
  }

  @Test
  void getStore_found_returnsStore() {
    when(service.findById(STORE_ID)).thenReturn(store);
    when(mapper.toRest(store)).thenReturn(response);

    BookStoreResponse result = controller.getStore(STORE_ID);

    assertEquals("Test Store", result.getName());
  }

  @Test
  void getStore_notFound_throws() {
    when(service.findById(STORE_ID)).thenThrow(new NotFoundException("Not found"));

    assertThrows(NotFoundException.class, () -> controller.getStore(STORE_ID));
  }
}