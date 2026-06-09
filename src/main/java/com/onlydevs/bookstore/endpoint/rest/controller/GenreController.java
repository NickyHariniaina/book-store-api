package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.Genre;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreRepository genreRepository;

  @GetMapping
  public ResponseEntity<List<Genre>> getAllGenres() {
    return ResponseEntity.ok(genreRepository.findAll());
  }

  @GetMapping("/{id}/books")
  public ResponseEntity<Genre> getGenreWithBooks(@PathVariable UUID id) {
    return ResponseEntity.ok(
        genreRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Genre", id)));
  }

  @PostMapping
  public ResponseEntity<Genre> createGenre(
      @RequestParam String name, @RequestParam(required = false) String description) {
    if (genreRepository.existsByNameIgnoreCase(name)) {
      throw new ConflictException("Genre already exists: " + name);
    }
    Genre genre = Genre.builder().name(name).description(description).build();
    return ResponseEntity.status(HttpStatus.CREATED).body(genreRepository.save(genre));
  }

  @PatchMapping("/{id}/rename")
  public ResponseEntity<Genre> renameGenre(@PathVariable UUID id, @RequestParam String name) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Genre", id));
    if (genreRepository.existsByNameIgnoreCase(name)) {
      throw new ConflictException("Genre name already taken: " + name);
    }
    genre.rename(name);
    return ResponseEntity.ok(genreRepository.save(genre));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Genre", id));
    genreRepository.delete(genre);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/revenue")
  public ResponseEntity<List<Object[]>> getRevenuePerGenre() {
    return ResponseEntity.ok(genreRepository.countBooksPerGenre());
  }
}
