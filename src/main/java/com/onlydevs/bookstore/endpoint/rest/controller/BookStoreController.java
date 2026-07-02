package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.service.BookStoreService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor
public class BookStoreController {

  private final BookStoreService bookStoreService;

  @GetMapping
  public ResponseEntity<Page<BookStoreResponse>> getAllStores(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    Sort.Direction direction = Sort.Direction.fromString(sortDir);
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.getAllStores(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookStoreResponse> getStoreById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.getStoreById(id));
  }

  @PostMapping
  public ResponseEntity<BookStoreResponse> createStore(
      @Valid @RequestBody CreateBookStoreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookStoreService.createStore(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookStoreResponse> updateStore(
      @PathVariable UUID id, @Valid @RequestBody UpdateBookStoreRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.updateStore(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
    bookStoreService.deleteStore(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
