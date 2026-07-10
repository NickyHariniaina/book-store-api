package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateBookRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookRequest;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.service.BookService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping
  public ResponseEntity<Page<BookSummaryResponse>> getAllBooks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    var direction = Direction.fromString(sortDir);
    var sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookDetailResponse> getBookById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(id));
  }

  @PostMapping
  public ResponseEntity<BookDetailResponse> createBook(
      @Valid @RequestBody CreateBookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookDetailResponse> updateBook(
      @PathVariable UUID id, @Valid @RequestBody UpdateBookRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    bookService.deleteBook(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/{id}/authors/{authorId}")
  public ResponseEntity<BookAuthorResponse> addAuthorToBook(
      @PathVariable UUID id, @PathVariable UUID authorId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookService.addAuthorToBook(id, authorId));
  }

  @DeleteMapping("/{id}/authors/{authorId}")
  public ResponseEntity<Void> removeAuthorFromBook(
      @PathVariable UUID id, @PathVariable UUID authorId) {
    bookService.removeAuthorFromBook(id, authorId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/{id}/genres/{genreId}")
  public ResponseEntity<Void> addGenreToBook(@PathVariable UUID id, @PathVariable UUID genreId) {
    bookService.addGenreToBook(id, genreId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{id}/genres/{genreId}")
  public ResponseEntity<Void> removeGenreFromBook(
      @PathVariable UUID id, @PathVariable UUID genreId) {
    bookService.removeGenreFromBook(id, genreId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
