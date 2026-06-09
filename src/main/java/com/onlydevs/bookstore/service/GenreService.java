package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.GenreResponse;
import com.onlydevs.bookstore.model.dto.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.mapper.GenreMapper;
import com.onlydevs.bookstore.repository.GenreRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;

  public List<GenreResponse> getAllGenres() {
    return genreRepository.findAll().stream().map(genreMapper::toResponse).toList();
  }

  public GenreResponse getGenreById(UUID id) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    return genreMapper.toResponse(genre);
  }

  public GenreResponse createGenre(CreateGenreRequest request) {
    if (genreRepository.existsByNameIgnoreCase(request.name)) {
      throw new ConflictException("Genre already exists: " + request.name);
    }
    Genre genre = genreMapper.toEntity(request);
    return genreMapper.toResponse(genreRepository.save(genre));
  }

  public GenreResponse renameGenre(UUID id, RenameGenreRequest request) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    if (genreRepository.existsByNameIgnoreCase(request.name)) {
      throw new ConflictException("Genre name already taken: " + request.name);
    }
    genre.rename(request.name);
    return genreMapper.toResponse(genreRepository.save(genre));
  }

  public void deleteGenre(UUID id) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    genreRepository.delete(genre);
  }

  @Transactional(readOnly = true)
  public List<BookSummaryResponse> getBooksByGenreId(UUID genreId) {
    Genre genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(() -> new NotFoundException("Genre", genreId));
    return genre.getBooks().stream().map(genreMapper::toBookSummaryResponse).toList();
  }

  public List<RevenuePerGenreResponse> getRevenuePerGenre() {
    return genreRepository.revenueByGenre().stream()
        .map(genreMapper::toRevenuePerGenreResponse)
        .toList();
  }
}
