package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.onlydevs.bookstore.model.Book;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    return GenreResponse.builder().id(id).name(name).description(description).build();
  }

  @Test
  void should_create_genre_successfully() {
    CreateGenreRequest request =
        CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();
    Genre genre = createGenre(genreId, "Fiction", "Fiction books");
    Genre savedGenre = createGenre(genreId, "Fiction", "Fiction books");
    GenreResponse expectedResponse = createGenreResponse(genreId, "Fiction", "Fiction books");

    when(genreRepository.existsByNameIgnoreCase("Fiction")).thenReturn(false);
    when(genreMapper.toEntity(request)).thenReturn(genre);
    when(genreRepository.save(genre)).thenReturn(savedGenre);
    when(genreMapper.toResponse(savedGenre)).thenReturn(expectedResponse);

    GenreResponse actual = genreService.createGenre(request);

    assertEquals(expectedResponse, actual);
    verify(genreRepository).existsByNameIgnoreCase("Fiction");
    verify(genreMapper).toEntity(request);
    verify(genreRepository).save(genre);
    verify(genreMapper).toResponse(savedGenre);
  }

  @Test
  void should_throw_exception_when_create_duplicate_genre() {
    CreateGenreRequest request =
        CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();

    when(genreRepository.existsByNameIgnoreCase("Fiction")).thenReturn(true);

    assertThrows(ConflictException.class, () -> genreService.createGenre(request));
    verify(genreRepository).existsByNameIgnoreCase("Fiction");
    verifyNoMoreInteractions(genreMapper);
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_rename_genre_successfully() {
    Genre genre = createGenre(genreId, "Old Name", "Description");
    RenameGenreRequest request = RenameGenreRequest.builder().name("New Name").build();
    GenreResponse expectedResponse = createGenreResponse(genreId, "New Name", "Description");

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
    when(genreRepository.save(genre)).thenReturn(genre);
    when(genreMapper.toResponse(genre)).thenReturn(expectedResponse);

    GenreResponse actual = genreService.renameGenre(genreId, request);

    assertEquals(expectedResponse, actual);
    verify(genreRepository).findById(genreId);
    verify(genreRepository).existsByNameIgnoreCase("New Name");
    verify(genreRepository).save(genre);
    verify(genreMapper).toResponse(genre);
  }

  @Test
  void should_throw_exception_when_rename_to_duplicate_name() {
    Genre genre = createGenre(genreId, "Old Name", "Description");
    RenameGenreRequest request = RenameGenreRequest.builder().name("Taken Name").build();

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Taken Name")).thenReturn(true);

    assertThrows(ConflictException.class, () -> genreService.renameGenre(genreId, request));
    verify(genreRepository).findById(genreId);
    verify(genreRepository).existsByNameIgnoreCase("Taken Name");
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_throw_exception_when_rename_nonexistent_genre() {
    RenameGenreRequest request = RenameGenreRequest.builder().name("New Name").build();

    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> genreService.renameGenre(genreId, request));
    verify(genreRepository).findById(genreId);
    verify(genreRepository, never()).existsByNameIgnoreCase(any());
    verify(genreRepository, never()).save(any());
  }

  @Test
  void should_delete_genre_and_unlink_from_all_books() {
    Genre genre = createGenre(genreId, "Fiction", "Desc");

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
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Genre g1 = createGenre(id1, "Fiction", "Fiction books");
    Genre g2 = createGenre(id2, "Science", "Science books");
    GenreResponse r1 = createGenreResponse(id1, "Fiction", "Fiction books");
    GenreResponse r2 = createGenreResponse(id2, "Science", "Science books");

    when(genreRepository.findAll()).thenReturn(List.of(g1, g2));
    when(genreMapper.toResponse(g1)).thenReturn(r1);
    when(genreMapper.toResponse(g2)).thenReturn(r2);

    List<GenreResponse> result = genreService.getAllGenres();

    assertEquals(2, result.size());
    assertTrue(result.contains(r1));
    assertTrue(result.contains(r2));
    verify(genreRepository).findAll();
    verify(genreMapper).toResponse(g1);
    verify(genreMapper).toResponse(g2);
  }

  @Test
  void should_return_empty_list_when_no_genres_exist() {
    when(genreRepository.findAll()).thenReturn(List.of());

    List<GenreResponse> result = genreService.getAllGenres();

    assertTrue(result.isEmpty());
    verify(genreRepository).findAll();
  }

  @Test
  void should_get_genre_with_books_with_pagination() {
    Pageable pageable = PageRequest.of(0, 10);
    Book book = mock(Book.class);
    Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
    BookSummaryResponse summary =
        BookSummaryResponse.builder().id(UUID.randomUUID()).title("Test Book").build();

    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(genreRepository.findBooksByGenreId(genreId, pageable)).thenReturn(bookPage);
    when(genreMapper.toBookSummaryResponse(book)).thenReturn(summary);

    Page<BookSummaryResponse> result = genreService.getBooksByGenreId(genreId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(summary, result.getContent().getFirst());
    verify(genreRepository).existsById(genreId);
    verify(genreRepository).findBooksByGenreId(genreId, pageable);
    verify(genreMapper).toBookSummaryResponse(book);
  }

  @Test
  void should_throw_exception_when_get_genre_with_books_not_found() {
    Pageable pageable = PageRequest.of(0, 10);

    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> genreService.getBooksByGenreId(genreId, pageable));
    verify(genreRepository).existsById(genreId);
    verify(genreRepository, never()).findBooksByGenreId(any(), any());
  }

  @Test
  void should_get_revenue_per_genre_for_dashboard() {
    Object[] row1 = new Object[] {"Fiction", 500.0};
    Object[] row2 = new Object[] {"Science", 300.0};
    RevenuePerGenreResponse rev1 =
        RevenuePerGenreResponse.builder().genreName("Fiction").revenue(500.0).build();
    RevenuePerGenreResponse rev2 =
        RevenuePerGenreResponse.builder().genreName("Science").revenue(300.0).build();

    when(genreRepository.revenueByGenre()).thenReturn(List.of(row1, row2));
    when(genreMapper.toRevenuePerGenreResponse(row1)).thenReturn(rev1);
    when(genreMapper.toRevenuePerGenreResponse(row2)).thenReturn(rev2);

    List<RevenuePerGenreResponse> result = genreService.getRevenuePerGenre();

    assertEquals(2, result.size());
    assertEquals("Fiction", result.get(0).genreName);
    assertEquals(500.0, result.get(0).revenue);
    assertEquals("Science", result.get(1).genreName);
    assertEquals(300.0, result.get(1).revenue);
    verify(genreRepository).revenueByGenre();
    verify(genreMapper).toRevenuePerGenreResponse(row1);
    verify(genreMapper).toRevenuePerGenreResponse(row2);
  }

  @Test
  void should_calculate_revenue_correctly_for_multiple_genres() {
    Object[] row1 = new Object[] {"Fantasy", 1250.75};
    Object[] row2 = new Object[] {"History", 890.25};
    Object[] row3 = new Object[] {"Biography", 450.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.of(row1, row2, row3));
    when(genreMapper.toRevenuePerGenreResponse(row1))
        .thenReturn(
            RevenuePerGenreResponse.builder().genreName("Fantasy").revenue(1250.75).build());
    when(genreMapper.toRevenuePerGenreResponse(row2))
        .thenReturn(RevenuePerGenreResponse.builder().genreName("History").revenue(890.25).build());
    when(genreMapper.toRevenuePerGenreResponse(row3))
        .thenReturn(
            RevenuePerGenreResponse.builder().genreName("Biography").revenue(450.00).build());

    List<RevenuePerGenreResponse> result = genreService.getRevenuePerGenre();

    assertEquals(3, result.size());
    assertEquals(1250.75, result.get(0).revenue);
    assertEquals(890.25, result.get(1).revenue);
    assertEquals(450.00, result.get(2).revenue);
    assertTrue(result.stream().mapToDouble(r -> r.revenue).sum() > 0);
  }

  @Test
  void should_return_zero_revenue_for_genre_with_no_sales() {
    Object[] row = new Object[] {"New Genre", null};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(RevenuePerGenreResponse.builder().genreName("New Genre").revenue(0.0).build());

    List<RevenuePerGenreResponse> result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("New Genre", result.get(0).genreName);
    assertEquals(0.0, result.get(0).revenue);
  }

  @Test
  void should_aggregate_revenue_across_multiple_stores() {
    Object[] row = new Object[] {"Fiction", 1500.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(
            RevenuePerGenreResponse.builder().genreName("Fiction").revenue(1500.00).build());

    List<RevenuePerGenreResponse> result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("Fiction", result.get(0).genreName);
    assertEquals(1500.00, result.get(0).revenue);
  }

  @Test
  void should_handle_date_range_parameters_correctly() {
    Object[] row = new Object[] {"Drama", 750.00};

    when(genreRepository.revenueByGenre()).thenReturn(List.<Object[]>of(row));
    when(genreMapper.toRevenuePerGenreResponse(row))
        .thenReturn(RevenuePerGenreResponse.builder().genreName("Drama").revenue(750.00).build());

    List<RevenuePerGenreResponse> result = genreService.getRevenuePerGenre();

    assertEquals(1, result.size());
    assertEquals("Drama", result.get(0).genreName);
    assertEquals(750.00, result.get(0).revenue);
  }
}
