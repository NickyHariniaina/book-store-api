package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.mapper.GenreMapper;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.GenreRepository;
import com.onlydevs.bookstore.repository.GenreRepository.GenreRevenue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;

  @Mock private GenreMapper genreMapper;

  @InjectMocks private GenreService genreService;

  private Genre genre;
  private GenreResponse genreResponse;
  private UUID genreId;

  @BeforeEach
  void setUp() {
    genreId = UUID.randomUUID();
    genre = Genre.builder().id(genreId).name("Fiction").description("Fiction books").build();
    genreResponse =
        GenreResponse.builder().id(genreId).name("Fiction").description("Fiction books").build();
  }

  @Test
  void create_genre_ok_when_name_not_taken() {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();
    given(genreRepository.existsByNameIgnoreCase("Fiction")).willReturn(false);
    given(genreMapper.toDomain(request)).willReturn(genre);
    given(genreRepository.save(genre)).willReturn(genre);
    given(genreMapper.toRest(genre)).willReturn(genreResponse);

    var actual = genreService.createGenre(request);

    assertEquals(genreResponse, actual);
    then(genreRepository).should().existsByNameIgnoreCase("Fiction");
    then(genreMapper).should().toDomain(request);
    then(genreRepository).should().save(genre);
    then(genreMapper).should().toRest(genre);
  }

  @Test
  void create_genre_ko_when_name_already_taken() {
    var request = CreateGenreRequest.builder().name("Fiction").build();
    given(genreRepository.existsByNameIgnoreCase("Fiction")).willReturn(true);

    assertThrows(ConflictException.class, () -> genreService.createGenre(request));
    then(genreRepository).should().existsByNameIgnoreCase("Fiction");
    then(genreMapper).shouldHaveNoInteractions();
    then(genreRepository).should(never()).save(any());
  }

  @Test
  void rename_genre_ok_when_new_name_not_taken() {
    var request = RenameGenreRequest.builder().name("Science").build();
    var renamedGenre =
        Genre.builder().id(genreId).name("Science").description("Fiction books").build();
    var renamedResponse =
        GenreResponse.builder().id(genreId).name("Science").description("Fiction books").build();

    given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));
    given(genreRepository.existsByNameIgnoreCase("Science")).willReturn(false);
    given(genreRepository.save(genre)).willReturn(renamedGenre);
    given(genreMapper.toRest(renamedGenre)).willReturn(renamedResponse);

    var actual = genreService.renameGenre(genreId, request);

    assertEquals(renamedResponse, actual);
    then(genreRepository).should().findById(genreId);
    then(genreRepository).should().existsByNameIgnoreCase("Science");
    then(genreRepository).should().save(genre);
    then(genreMapper).should().toRest(renamedGenre);
  }

  @Test
  void rename_genre_ko_when_new_name_already_taken() {
    var request = RenameGenreRequest.builder().name("Taken").build();

    given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));
    given(genreRepository.existsByNameIgnoreCase("Taken")).willReturn(true);

    assertThrows(ConflictException.class, () -> genreService.renameGenre(genreId, request));
    then(genreRepository).should().findById(genreId);
    then(genreRepository).should().existsByNameIgnoreCase("Taken");
    then(genreRepository).should(never()).save(any());
  }

  @Test
  void rename_genre_ko_when_genre_not_found() {
    var request = RenameGenreRequest.builder().name("Science").build();

    given(genreRepository.findById(genreId)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> genreService.renameGenre(genreId, request));
    then(genreRepository).should().findById(genreId);
    then(genreRepository).should(never()).existsByNameIgnoreCase(any());
    then(genreRepository).should(never()).save(any());
  }

  @Test
  void delete_genre_ok_when_exists() {
    given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));

    genreService.deleteGenre(genreId);

    then(genreRepository).should().findById(genreId);
    then(genreRepository).should().delete(genre);
  }

  @Test
  void delete_genre_ko_when_not_found() {
    given(genreRepository.findById(genreId)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> genreService.deleteGenre(genreId));
    then(genreRepository).should().findById(genreId);
    then(genreRepository).should(never()).delete(any());
  }

  @Test
  void get_all_genres_ok_when_genres_exist() {
    var pageable = PageRequest.of(0, 10);
    var g1 = Genre.builder().id(genreId).name("Fiction").build();
    var r1 = GenreResponse.builder().id(genreId).name("Fiction").build();
    var genrePage = new PageImpl<>(List.of(g1), pageable, 1);

    given(genreRepository.findAll(pageable)).willReturn(genrePage);
    given(genreMapper.toRest(g1)).willReturn(r1);

    var result = genreService.getAllGenres(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(r1, result.getContent().getFirst());
    then(genreRepository).should().findAll(pageable);
    then(genreMapper).should().toRest(g1);
  }

  @Test
  void get_all_genres_ok_when_no_genres() {
    var pageable = PageRequest.of(0, 10);
    var emptyPage = Page.<Genre>empty(pageable);

    given(genreRepository.findAll(pageable)).willReturn(emptyPage);

    var result = genreService.getAllGenres(pageable);

    assertTrue(result.isEmpty());
    then(genreRepository).should().findAll(pageable);
  }

  @Test
  void get_books_by_genre_ok_when_genre_exists() {
    var pageable = PageRequest.of(0, 10);
    var book = mock(Book.class);
    var bookPage = new PageImpl<>(List.of(book), pageable, 1);
    var summary = BookSummaryResponse.builder().id(UUID.randomUUID()).title("Test Book").build();

    given(genreRepository.existsById(genreId)).willReturn(true);
    given(genreRepository.findBooksByGenreId(genreId, pageable)).willReturn(bookPage);
    given(genreMapper.toBookSummaryResponse(book)).willReturn(summary);

    var result = genreService.getBooksByGenreId(genreId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(summary, result.getContent().getFirst());
    then(genreRepository).should().existsById(genreId);
    then(genreRepository).should().findBooksByGenreId(genreId, pageable);
    then(genreMapper).should().toBookSummaryResponse(book);
  }

  @Test
  void get_books_by_genre_ko_when_genre_not_found() {
    var pageable = PageRequest.of(0, 10);

    given(genreRepository.existsById(genreId)).willReturn(false);

    assertThrows(NotFoundException.class, () -> genreService.getBooksByGenreId(genreId, pageable));
    then(genreRepository).should().existsById(genreId);
    then(genreRepository).should(never()).findBooksByGenreId(any(), any());
  }

  @Test
  void get_revenue_per_genre_ok_when_data_exists() {
    var row = mock(GenreRevenue.class);
    given(row.getName()).willReturn("Fiction");
    given(row.getRevenue()).willReturn(BigDecimal.valueOf(500.0));
    var revenueResponse =
        RevenuePerGenreResponse.builder()
            .genreName("Fiction")
            .revenue(BigDecimal.valueOf(500.0))
            .build();

    given(genreRepository.revenueByGenre()).willReturn(List.of(row));
    given(genreMapper.toRevenuePerGenreResponse(row)).willReturn(revenueResponse);

    var result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("Fiction", result.getFirst().getGenreName());
    assertEquals(BigDecimal.valueOf(500.0), result.getFirst().getRevenue());
    then(genreRepository).should().revenueByGenre();
    then(genreMapper).should().toRevenuePerGenreResponse(row);
  }

  @Test
  void get_revenue_per_genre_ok_when_no_data() {
    given(genreRepository.revenueByGenre()).willReturn(List.of());

    var result = genreService.getRevenuePerGenre();

    assertTrue(result.isEmpty());
    then(genreRepository).should().revenueByGenre();
  }

  @Test
  void get_all_genres_should_use_correct_pageable_parameters() {
    var customPageable = PageRequest.of(2, 15);

    given(genreRepository.findAll(customPageable)).willReturn(Page.empty());

    genreService.getAllGenres(customPageable);

    then(genreRepository).should().findAll(customPageable);
  }

  @Test
  void get_all_genres_when_repository_throws_exception_should_propagate() {
    var pageable = PageRequest.of(0, 10);
    var exception = new RuntimeException("Database error");

    given(genreRepository.findAll(pageable)).willThrow(exception);

    assertThrows(RuntimeException.class, () -> genreService.getAllGenres(pageable));
    then(genreRepository).should().findAll(pageable);
    then(genreMapper).shouldHaveNoInteractions();
  }
}
