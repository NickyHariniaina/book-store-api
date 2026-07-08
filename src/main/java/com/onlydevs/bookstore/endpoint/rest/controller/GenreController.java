package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.service.GenreService;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @GetMapping
  public ResponseEntity<Page<GenreResponse>> getAllGenres(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    Direction direction = Direction.fromString(sortDir);
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(genreService.getAllGenres(pageable));
  }

  @GetMapping("/{id}/books")
  public ResponseEntity<Page<BookSummaryResponse>> getBooksByGenreId(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(genreService.getBooksByGenreId(id, pageable));
  }

  @PostMapping
  public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody CreateGenreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenre(request));
  }

  @PatchMapping("/{id}/rename")
  public ResponseEntity<GenreResponse> renameGenre(
      @PathVariable UUID id, @Valid @RequestBody RenameGenreRequest request) {
    return ResponseEntity.ok(genreService.renameGenre(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
    genreService.deleteGenre(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/revenue")
  public ResponseEntity<List<RevenuePerGenreResponse>> getRevenuePerGenre() {
    return ResponseEntity.ok(genreService.getRevenuePerGenre());
  }
}
