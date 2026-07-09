package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.GenreMapper;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.GenreRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;

  public Page<GenreResponse> getAllGenres(Pageable pageable) {
    return genreRepository.findAll(pageable).map(genreMapper::toRest);
  }

  public GenreResponse createGenre(CreateGenreRequest request) {
    if (genreRepository.existsByNameIgnoreCase(request.getName())) {
      throw new ConflictException("Genre already exists: " + request.getName());
    }
    var genre = genreMapper.toDomain(request);
    return genreMapper.toRest(genreRepository.save(genre));
  }

  public GenreResponse renameGenre(UUID id, RenameGenreRequest request) {
    var genre =
        genreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Genre not found with id: " + id));
    if (genreRepository.existsByNameIgnoreCase(request.getName())) {
      throw new ConflictException("Genre name already taken: " + request.getName());
    }
    genre.rename(request.getName());
    return genreMapper.toRest(genreRepository.save(genre));
  }

  public void deleteGenre(UUID id) {
    var genre =
        genreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Genre not found with id: " + id));
    genreRepository.delete(genre);
  }

  public Page<BookSummaryResponse> getBooksByGenreId(UUID genreId, Pageable pageable) {
    if (!genreRepository.existsById(genreId)) {
      throw new NotFoundException("Genre not found with id: " + genreId);
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
