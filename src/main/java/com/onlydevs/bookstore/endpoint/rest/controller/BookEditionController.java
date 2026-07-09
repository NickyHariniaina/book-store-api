package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.service.BookEditionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BookEditionController {
  private final BookEditionService bookEditionService;

  @GetMapping("/books/{bookId}/editions")
  public ResponseEntity<List<BookEditionResponse>> getEditionsByBookId(@PathVariable UUID bookId) {
    return ResponseEntity.ok(bookEditionService.getEditionsByBookId(bookId));
  }

  @GetMapping("/editions/{id}")
  public ResponseEntity<BookEditionResponse> getEditionById(@PathVariable UUID id) {
    return ResponseEntity.ok(bookEditionService.getEditionById(id));
  }

  @GetMapping("/editions/isbn/{isbn}")
  public ResponseEntity<BookEditionResponse> getEditionByIsbn(@PathVariable String isbn) {
    return ResponseEntity.ok(bookEditionService.getEditionByIsbn(isbn));
  }

  @PostMapping("/books/{bookId}/editions")
  public ResponseEntity<BookEditionResponse> createEdition(
      @PathVariable UUID bookId, @Valid @RequestBody CreateBookEditionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookEditionService.createEdition(bookId, request));
  }

  @PutMapping("/editions/{id}")
  public ResponseEntity<BookEditionResponse> updateEdition(
      @PathVariable UUID id, @Valid @RequestBody UpdateBookEditionRequest request) {
    return ResponseEntity.ok(bookEditionService.updateEdition(id, request));
  }

  @PatchMapping("/editions/{id}/activate")
  public ResponseEntity<BookEditionResponse> activateEdition(@PathVariable UUID id) {
    return ResponseEntity.ok(bookEditionService.activateEdition(id));
  }

  @PatchMapping("/editions/{id}/deactivate")
  public ResponseEntity<BookEditionResponse> deactivateEdition(@PathVariable UUID id) {
    return ResponseEntity.ok(bookEditionService.deactivateEdition(id));
  }
}
