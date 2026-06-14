package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.endpoint.rest.model.BookSummaryResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.GenreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.RenameGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.mapper.GenreMapper;
import com.onlydevs.bookstore.repository.GenreRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;

  @Mock private GenreMapper genreMapper;

  @InjectMocks private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final Instant now = Instant.now();

  private Genre createGenre(UUID id, String name, String description) {
    return Genre.builder().id(id).name(name).description(description).build();
  }

  private GenreResponse createGenreResponse(UUID id, String name, String description) {
    return new GenreResponse().id(id).name(name).description(description);
  }

  @Test
  void should_create_genre_successfully() {
    var request = new CreateGenreRequest().name("Fiction").description("Fiction books");
    var genre = createGenre(genreId, "Fiction", "Fiction books");
    var savedGenre = createGenre(genreId, "Fiction", "Fiction books");
    var expectedResponse = createGenreResponse(genreId, "Fiction", "Fiction books");

    when(genreRepository.existsByNameIgnoreCase("Fiction")).thenReturn(false);
    when(genreMapper.toDomain(request)).thenReturn(genre);
    when(genreRepository.save(genre)).thenReturn(savedGenre);
    when(genreMapper.toRest(savedGenre)).thenReturn(expectedResponse);

    var actual = genreService.createGenre(request);

    assertEquals(expectedResponse, actual);
    verify(genreRepository).existsByNameIgnoreCase("Fiction");
    verify(genreMapper).toDomain(request);
    verify(genreRepository).save(genre);
    verify(genreMapper).toRest(savedGenre);
  }

  @Test
  void should_throw_exception_when_create_duplicate_genre() {
    var request = new CreateGenreRequest().name("Fiction").description("Fiction books");

    when(genreRepository.existsByNameIgnoreCase("Fiction")).thenReturn(true);

    assertThrows(ConflictException.class, () -> genreService.createGenre(request));
    verify(genreRepository).existsByNameIgnoreCase("Fiction");
    verifyNoMoreInteractions(genreMapper);
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_rename_genre_successfully() {
    var genre = createGenre(genreId, "Old Name", "Description");
    var request = new RenameGenreRequest().name("New Name");
    var expectedResponse = createGenreResponse(genreId, "New Name", "Description");

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
    when(genreRepository.save(genre)).thenReturn(genre);
    when(genreMapper.toRest(genre)).thenReturn(expectedResponse);

    var actual = genreService.renameGenre(genreId, request);

    assertEquals(expectedResponse, actual);
    verify(genreRepository).findById(genreId);
    verify(genreRepository).existsByNameIgnoreCase("New Name");
    verify(genreRepository).save(genre);
    verify(genreMapper).toRest(genre);
  }

  @Test
  void should_throw_exception_when_rename_to_duplicate_name() {
    var genre = createGenre(genreId, "Old Name", "Description");
    var request = new RenameGenreRequest().name("Taken Name");

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Taken Name")).thenReturn(true);

    assertThrows(ConflictException.class, () -> genreService.renameGenre(genreId, request));
    verify(genreRepository).findById(genreId);
    verify(genreRepository).existsByNameIgnoreCase("Taken Name");
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_throw_exception_when_rename_nonexistent_genre() {
    var request = new RenameGenreRequest().name("New Name");

    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> genreService.renameGenre(genreId, request));
    verify(genreRepository).findById(genreId);
    verify(genreRepository, never()).existsByNameIgnoreCase(any());
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_delete_genre_and_unlink_from_all_books() {
    var genre = createGenre(genreId, "Fiction", "Desc");

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.deleteGenre(genreId);

    verify(genreRepository).findById(genreId);
    verify(genreRepository).delete(genre);
  }

  @Test
  void should_throw_exception_when_delete_nonexistent_genre() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> genreService.deleteGenre(genreId));
    verify(genreRepository).findById(genreId);
    verify(genreRepository, never()).delete(any());
  }

  @Test
  void should_get_all_genres_and_return_full_list() {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var g1 = createGenre(id1, "Fiction", "Fiction books");
    var g2 = createGenre(id2, "Science", "Science books");
    var r1 = createGenreResponse(id1, "Fiction", "Fiction books");
    var r2 = createGenreResponse(id2, "Science", "Science books");

    when(genreRepository.findAll()).thenReturn(List.of(g1, g2));
    when(genreMapper.toRest(g1)).thenReturn(r1);
    when(genreMapper.toRest(g2)).thenReturn(r2);

    var result = genreService.getAllGenres();

    assertEquals(2, result.size());
    assertTrue(result.contains(r1));
    assertTrue(result.contains(r2));
    verify(genreRepository).findAll();
    verify(genreMapper).toRest(g1);
    verify(genreMapper).toRest(g2);
  }

  @Test
  void should_return_empty_list_when_no_genres_exist() {
    when(genreRepository.findAll()).thenReturn(List.of());

    var result = genreService.getAllGenres();

    assertTrue(result.isEmpty());
    verify(genreRepository).findAll();
  }

  @Test
  void should_get_genre_with_books_with_pagination() {
    var pageable = PageRequest.of(0, 10);
    var book = mock(Book.class);
    var bookPage = new PageImpl<>(List.of(book), pageable, 1);
    var summary = new BookSummaryResponse().id(UUID.randomUUID()).title("Test Book");

    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(genreRepository.findBooksByGenreId(genreId, pageable)).thenReturn(bookPage);
    when(genreMapper.toBookSummaryResponse(book)).thenReturn(summary);

    var result = genreService.getBooksByGenreId(genreId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(summary, result.getContent().getFirst());
    verify(genreRepository).existsById(genreId);
    verify(genreRepository).findBooksByGenreId(genreId, pageable);
    verify(genreMapper).toBookSummaryResponse(book);
  }

  @Test
  void should_throw_exception_when_get_genre_with_books_not_found() {
    var pageable = PageRequest.of(0, 10);

    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> genreService.getBooksByGenreId(genreId, pageable));
    verify(genreRepository).existsById(genreId);
    verify(genreRepository, never()).findBooksByGenreId(any(), any());
  }

  @Test
  void should_get_revenue_per_genre_for_dashboard() {
    var row1 = new Object[] {"Fiction", 500.0};
    var row2 = new Object[] {"Science", 300.0};
    var rev1 = new RevenuePerGenreResponse().genreName("Fiction").revenue(BigDecimal.valueOf(500.0));
    var rev2 = new RevenuePerGenreResponse().genreName("Science").revenue(BigDecimal.valueOf(300.0));

    when(genreRepository.revenueByGenre()).thenReturn(List.of(row1, row2));
    when(genreMapper.toRevenuePerGenreResponse(row1)).thenReturn(rev1);
    when(genreMapper.toRevenuePerGenreResponse(row2)).thenReturn(rev2);

    var result = genreService.getRevenuePerGenre();

    assertEquals(2, result.size());
    assertEquals("Fiction", result.get(0).getGenreName());
    assertEquals(BigDecimal.valueOf(500.0), result.get(0).getRevenue());
    assertEquals("Science", result.get(1).getGenreName());
    assertEquals(BigDecimal.valueOf(300.0), result.get(1).getRevenue());
    verify(genreRepository).revenueByGenre();
    verify(genreMapper).toRevenuePerGenreResponse(row1);
    verify(genreMapper).toRevenuePerGenreResponse(row2);
  }

  @Test
  void should_calculate_revenue_correctly_for_multiple_genres() {
    var row1 = new Object[] {"Fantasy", 1250.75};
    var row2 = new Object[] {"History", 890.25};
    var row3 = new Object[] {"Biography", 450.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.of(row1, row2, row3));
    when(genreMapper.toRevenuePerGenreResponse(row1))
        .thenReturn(
            new RevenuePerGenreResponse().genreName("Fantasy").revenue(BigDecimal.valueOf(1250.75)));
    when(genreMapper.toRevenuePerGenreResponse(row2))
        .thenReturn(new RevenuePerGenreResponse().genreName("History").revenue(BigDecimal.valueOf(890.25)));
    when(genreMapper.toRevenuePerGenreResponse(row3))
        .thenReturn(
            new RevenuePerGenreResponse().genreName("Biography").revenue(BigDecimal.valueOf(450.00)));

    var result = genreService.getRevenuePerGenre();

    assertEquals(3, result.size());
    assertEquals(BigDecimal.valueOf(1250.75), result.get(0).getRevenue());
    assertEquals(BigDecimal.valueOf(890.25), result.get(1).getRevenue());
    assertEquals(BigDecimal.valueOf(450.00), result.get(2).getRevenue());
    assertTrue(result.stream().mapToDouble(r -> r.getRevenue().doubleValue()).sum() > 0);
  }

  @Test
  void should_return_zero_revenue_for_genre_with_no_sales() {
    var row = new Object[] {"New Genre", null};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(new RevenuePerGenreResponse().genreName("New Genre").revenue(BigDecimal.ZERO));

    var result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("New Genre", result.get(0).getGenreName());
    assertEquals(BigDecimal.ZERO, result.get(0).getRevenue());
  }

  @Test
  void should_aggregate_revenue_across_multiple_stores() {
    var row = new Object[] {"Fiction", 1500.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(
            new RevenuePerGenreResponse().genreName("Fiction").revenue(BigDecimal.valueOf(1500.00)));

    var result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("Fiction", result.get(0).getGenreName());
    assertEquals(BigDecimal.valueOf(1500.00), result.get(0).getRevenue());
  }

  @Test
  void should_handle_date_range_parameters_correctly() {
    var row = new Object[] {"Drama", 750.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(new RevenuePerGenreResponse().genreName("Drama").revenue(BigDecimal.valueOf(750.00)));

    var result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("Drama", result.get(0).getGenreName());
    assertEquals(BigDecimal.valueOf(750.00), result.get(0).getRevenue());
  }
}
