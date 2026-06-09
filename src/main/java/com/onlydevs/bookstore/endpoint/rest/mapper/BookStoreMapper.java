package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.endpoint.rest.model.BookStoreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.BookStore;
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
    return new BookStoreResponse()
        .id(bookStore.getId())
        .name(bookStore.getName())
        .address(bookStore.getAddress())
        .phone(bookStore.getPhone())
        .email(bookStore.getEmail())
        .createdAt(bookStore.getCreatedAt())
        .updatedAt(bookStore.getUpdatedAt());
  }

  public List<BookStoreResponse> toRestList(List<BookStore> bookStores) {
    return bookStores.stream().map(this::toRest).toList();
  }
}
