package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookStoreServiceTest {

  BookStoreRepository repository = mock(BookStoreRepository.class);
  BookStoreService subject = new BookStoreService(repository);

  BookStore store =
      BookStore.builder()
          .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
          .name("Test Store")
          .address("123 Test St")
          .phone("0340000000")
          .email("test@store.com")
          .build();

  @Test
  void findAll_returnsList() {
    when(repository.findAll()).thenReturn(List.of(store));

    List<BookStore> result = subject.findAll();

    assertEquals(1, result.size());
    assertEquals("Test Store", result.get(0).getName());
  }

  @Test
  void findById_found_returnsStore() {
    when(repository.findById(store.getId())).thenReturn(Optional.of(store));

    BookStore result = subject.findById(store.getId());

    assertEquals("Test Store", result.getName());
  }

  @Test
  void findById_notFound_throws() {
    when(repository.findById(store.getId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> subject.findById(store.getId()));
  }

  @Test
  void save_returnsSavedStore() {
    when(repository.save(store)).thenReturn(store);

    BookStore result = subject.save(store);

    assertEquals("Test Store", result.getName());
  }

  @Test
  void update_found_appliesFields() {
    String newName = "Updated Store";
    when(repository.findById(store.getId())).thenReturn(Optional.of(store));
    when(repository.save(store)).thenReturn(store);

    BookStore result = subject.update(store.getId(), newName, null, null, null);

    assertEquals(newName, result.getName());
  }
}