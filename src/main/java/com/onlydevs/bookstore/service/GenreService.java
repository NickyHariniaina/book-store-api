package com.onlydevs.bookstore.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public GenreResponse createGenre(CreateGenreRequest request) {
    if (genreRepository.existsByNameIgnoreCase(request.name)) {
      throw new ConflictException("Genre already exists: " + request.name);
    }
    var genre = genreMapper.toEntity(request);
    return genreMapper.toResponse(genreRepository.save(genre));
  }

  public GenreResponse renameGenre(UUID id, RenameGenreRequest request) {
    var genre = genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    if (genreRepository.existsByNameIgnoreCase(request.name)) {
      throw new ConflictException("Genre name already taken: " + request.name);
    }
    genre.rename(request.name);
    return genreMapper.toResponse(genreRepository.save(genre));
  }

  public void deleteGenre(UUID id) {
    var genre = genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    genreRepository.delete(genre);
  }

  @Transactional(readOnly = true)
  public Page<BookSummaryResponse> getBooksByGenreId(UUID genreId, Pageable pageable) {
    if (!genreRepository.existsById(genreId)) {
      throw new NotFoundException("Genre", genreId);
    }
    return genreRepository
        .findBooksByGenreId(genreId, pageable)
        .map(genreMapper::toBookSummaryResponse);
  }

  public List<RevenuePerGenreResponse> getRevenuePerGenre() {
    return genreRepository.revenueByGenre().stream()
        .map(genreMapper::toRevenuePerGenreResponse)
        .toList();
  }
}
