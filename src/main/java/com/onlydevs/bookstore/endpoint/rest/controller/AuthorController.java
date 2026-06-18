package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/authors")
public class AuthorController {
  private final AuthorService authorService;

  @PostMapping
  public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request));
  }

  @GetMapping
  public ResponseEntity<Page<AuthorResponse>> getAllAuthors(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    Sort.Direction direction = Sort.Direction.fromString(sortDir);
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(authorService.getAllAuthors(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponse> getById(@PathVariable String id) {
    return ResponseEntity.ok(authorService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AuthorResponse> update(@PathVariable String id, @RequestBody UpdateAuthorRequest request) {
    return ResponseEntity.ok(authorService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    authorService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
