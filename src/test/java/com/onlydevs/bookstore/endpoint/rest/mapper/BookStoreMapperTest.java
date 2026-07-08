package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookStoreMapperTest {

  private final BookStoreMapper mapper = new BookStoreMapper();

  @Test
  void toDomain_should_map_request_to_bookstore() {
    var request =
        CreateBookStoreRequest.builder()
            .name("Mata Store")
            .address("123 Mata St")
            .phone("0340000000")
            .email("mata@cute.com")
            .build();

    BookStore result = mapper.toDomain(request);

    assertNull(result.getId());
    assertEquals("Mata Store", result.getName());
    assertEquals("123 Mata St", result.getAddress());
    assertEquals("0340000000", result.getPhone());
    assertEquals("mata@cute.com", result.getEmail());
    assertNull(result.getCreatedAt());
    assertNull(result.getUpdatedAt());
  }

  @Test
  void toDomain_should_map_minimal_request() {
    var request = CreateBookStoreRequest.builder().name("Mata Store").build();

    BookStore result = mapper.toDomain(request);

    assertEquals("Mata Store", result.getName());
    assertNull(result.getAddress());
    assertNull(result.getPhone());
    assertNull(result.getEmail());
  }

  @Test
  void toRest_should_map_all_fields() {
    var id = UUID.randomUUID();
    var now = Instant.now();
    var bookStore =
        BookStore.builder()
            .id(id)
            .name("Mata Store")
            .address("123 Mata St")
            .phone("0340000000")
            .email("mata@cute.com")
            .createdAt(now)
            .updatedAt(now)
            .build();

    BookStoreResponse result = mapper.toRest(bookStore);

    assertEquals(id, result.getId());
    assertEquals("Mata Store", result.getName());
    assertEquals("123 Mata St", result.getAddress());
    assertEquals("0340000000", result.getPhone());
    assertEquals("mata@cute.com", result.getEmail());
    assertEquals(now, result.getCreatedAt());
    assertEquals(now, result.getUpdatedAt());
  }

  @Test
  void toRest_should_return_null_when_bookstore_is_null() {
    assertNull(mapper.toRest(null));
  }

  @Test
  void toRest_should_map_nullable_fields() {
    var bookStore =
        BookStore.builder()
            .id(UUID.randomUUID())
            .name("cute Store")
            .createdAt(Instant.now())
            .build();

    BookStoreResponse result = mapper.toRest(bookStore);

    assertNull(result.getAddress());
    assertNull(result.getPhone());
    assertNull(result.getEmail());
    assertNull(result.getUpdatedAt());
  }

  @Test
  void toRestList_should_map_list() {
    var store1 =
        BookStore.builder().id(UUID.randomUUID()).name("Mata").createdAt(Instant.now()).build();
    var store2 =
        BookStore.builder().id(UUID.randomUUID()).name("cute").createdAt(Instant.now()).build();
    var stores = List.of(store1, store2);

    var result = mapper.toRestList(stores);

    assertEquals(2, result.size());
    assertEquals("Mata", result.get(0).getName());
    assertEquals("cute", result.get(1).getName());
  }

  @Test
  void toRestList_should_return_empty_when_list_is_empty() {
    var result = mapper.toRestList(List.of());

    assertTrue(result.isEmpty());
  }
}
