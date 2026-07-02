package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookStoreMapper {

  public BookStore toDomain(CreateBookStoreRequest request) {
    return BookStore.builder()
        .name(request.getName())
        .address(request.getAddress())
        .phone(request.getPhone())
        .email(request.getEmail())
        .build();
  }

  public BookStoreResponse toRest(BookStore bookStore) {
    if (bookStore == null) {
      return null;
    }
    return BookStoreResponse.builder()
        .id(bookStore.getId())
        .name(bookStore.getName())
        .address(bookStore.getAddress())
        .phone(bookStore.getPhone())
        .email(bookStore.getEmail())
        .createdAt(bookStore.getCreatedAt())
        .updatedAt(bookStore.getUpdatedAt())
        .build();
  }

  public List<BookStoreResponse> toRestList(List<BookStore> bookStores) {
    return bookStores.stream().map(this::toRest).toList();
  }
}
