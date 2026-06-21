package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookStoreMapper;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookStoreServiceTest {

  @Mock private BookStoreRepository bookStoreRepository;

  @Mock private BookStoreMapper bookStoreMapper;

  @InjectMocks private BookStoreService bookStoreService;

  private UUID storeId;
  private BookStore bookStore;
  private BookStoreResponse bookStoreResponse;

  @BeforeEach
  void setUp() {
    storeId = UUID.randomUUID();
    bookStore =
        BookStore.builder()
            .id(storeId)
            .name("Test Store")
            .address("123 Test St")
            .phone("0340000000")
            .email("test@store.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    bookStoreResponse =
        BookStoreResponse.builder()
            .id(storeId)
            .name("Test Store")
            .address("123 Test St")
            .phone("0340000000")
            .email("test@store.com")
            .createdAt(bookStore.getCreatedAt())
            .updatedAt(bookStore.getUpdatedAt())
            .build();
  }

  @Test
  void get_all_stores_should_return_page_of_stores() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<BookStore> storePage = new PageImpl<>(List.of(bookStore), pageable, 1);

    given(bookStoreRepository.findAll(pageable)).willReturn(storePage);
    given(bookStoreMapper.toRest(bookStore)).willReturn(bookStoreResponse);

    Page<BookStoreResponse> result = bookStoreService.getAllStores(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Test Store");
    assertThat(result.getTotalElements()).isEqualTo(1);

    then(bookStoreRepository).should().findAll(pageable);
    then(bookStoreMapper).should().toRest(bookStore);
  }

  @Test
  void get_all_stores_when_empty_should_return_empty_page() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<BookStore> emptyPage = Page.empty(pageable);

    given(bookStoreRepository.findAll(pageable)).willReturn(emptyPage);

    Page<BookStoreResponse> result = bookStoreService.getAllStores(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();

    then(bookStoreRepository).should().findAll(pageable);
    then(bookStoreMapper).should(never()).toRest(any());
  }

  @Test
  void get_store_by_id_when_found_should_return_store() {
    given(bookStoreRepository.findById(storeId)).willReturn(Optional.of(bookStore));
    given(bookStoreMapper.toRest(bookStore)).willReturn(bookStoreResponse);

    BookStoreResponse result = bookStoreService.getStoreById(storeId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(storeId);
    assertThat(result.getName()).isEqualTo("Test Store");

    then(bookStoreRepository).should().findById(storeId);
    then(bookStoreMapper).should().toRest(bookStore);
  }

  @Test
  void get_store_by_id_when_not_found_should_throw() {
    given(bookStoreRepository.findById(storeId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookStoreService.getStoreById(storeId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookStore not found");

    then(bookStoreRepository).should().findById(storeId);
    then(bookStoreMapper).should(never()).toRest(any());
  }

  @Test
  void create_store_should_persist_and_return() {
    CreateBookStoreRequest request =
        CreateBookStoreRequest.builder()
            .name("New Store")
            .address("456 New St")
            .phone("0340000001")
            .email("new@store.com")
            .build();

    given(bookStoreMapper.toDomain(request)).willReturn(bookStore);
    given(bookStoreRepository.save(bookStore)).willReturn(bookStore);
    given(bookStoreMapper.toRest(bookStore)).willReturn(bookStoreResponse);

    BookStoreResponse result = bookStoreService.createStore(request);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Test Store");

    then(bookStoreMapper).should().toDomain(request);
    then(bookStoreRepository).should().save(bookStore);
    then(bookStoreMapper).should().toRest(bookStore);
  }

  @Test
  void update_store_when_found_should_modify_and_return() {
    UpdateBookStoreRequest request =
        UpdateBookStoreRequest.builder().name("Updated Store").build();

    given(bookStoreRepository.findById(storeId)).willReturn(Optional.of(bookStore));
    given(bookStoreRepository.save(bookStore)).willReturn(bookStore);
    given(bookStoreMapper.toRest(bookStore)).willReturn(bookStoreResponse);

    BookStoreResponse result = bookStoreService.updateStore(storeId, request);

    assertThat(result).isNotNull();

    then(bookStoreRepository).should().findById(storeId);
    then(bookStoreRepository).should().save(bookStore);
    then(bookStoreMapper).should().toRest(bookStore);
  }

  @Test
  void update_store_when_not_found_should_throw() {
    UpdateBookStoreRequest request = UpdateBookStoreRequest.builder().name("Updated").build();

    given(bookStoreRepository.findById(storeId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookStoreService.updateStore(storeId, request))
        .isInstanceOf(NotFoundException.class);

    then(bookStoreRepository).should().findById(storeId);
    then(bookStoreRepository).should(never()).save(any());
  }

  @Test
  void delete_store_when_found_should_remove() {
    given(bookStoreRepository.existsById(storeId)).willReturn(true);

    bookStoreService.deleteStore(storeId);

    then(bookStoreRepository).should().existsById(storeId);
    then(bookStoreRepository).should().deleteById(storeId);
  }

  @Test
  void delete_store_when_not_found_should_throw() {
    given(bookStoreRepository.existsById(storeId)).willReturn(false);

    assertThatThrownBy(() -> bookStoreService.deleteStore(storeId))
        .isInstanceOf(NotFoundException.class);

    then(bookStoreRepository).should().existsById(storeId);
    then(bookStoreRepository).should(never()).deleteById(any());
  }
}
