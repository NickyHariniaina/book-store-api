package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.GenreResponse;
import com.onlydevs.bookstore.model.dto.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.RevenuePerGenreResponse;
import com.onlydevs.bookstore.service.GenreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @GetMapping
  public ResponseEntity<List<GenreResponse>> getAllGenres() {
    return ResponseEntity.ok(genreService.getAllGenres());
  }

  @GetMapping("/{id}/books")
  public ResponseEntity<List<BookSummaryResponse>> getBooksByGenreId(@PathVariable UUID id) {
    return ResponseEntity.ok(genreService.getBooksByGenreId(id));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenreResponse> getGenreById(@PathVariable UUID id) {
    return ResponseEntity.ok(genreService.getGenreById(id));
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
