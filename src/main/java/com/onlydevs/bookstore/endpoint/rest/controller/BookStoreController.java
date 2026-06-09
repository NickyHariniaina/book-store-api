package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookStoreMapper;
import com.onlydevs.bookstore.endpoint.rest.model.BookStoreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateBookStoreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateBookStoreRequest;
import com.onlydevs.bookstore.service.BookStoreService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor
public class BookStoreController {

  private final BookStoreService service;
  private final BookStoreMapper mapper;

  @GetMapping
  public java.util.List<BookStoreResponse> getStores() {
    return mapper.toRestList(service.findAll());
  }

  @GetMapping("/{id}")
  public BookStoreResponse getStore(@PathVariable UUID id) {
    return mapper.toRest(service.findById(id));
  }

  @PostMapping
  public ResponseEntity<BookStoreResponse> createStore(
      @RequestBody CreateBookStoreRequest request) {
    var bookStore = service.save(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRest(bookStore));
  }

  @PutMapping("/{id}")
  public BookStoreResponse updateStore(
      @PathVariable UUID id, @RequestBody UpdateBookStoreRequest request) {
    var bookStore =
        service.update(id, request.getName(), request.getAddress(), request.getPhone(),
            request.getEmail());
    return mapper.toRest(bookStore);
  }
}