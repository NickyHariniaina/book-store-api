package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookAuthor;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.enums.AuthorRole;
import com.onlydevs.bookstore.model.enums.BookLanguage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GenreMapperTest {

  private final AuthorMapper.GenreMapper mapper = new AuthorMapper.GenreMapper();

  @Test
  void toResponse_maps_all_fields() {
    var id = UUID.randomUUID();
    var genre = Genre.builder().id(id).name("Fiction").description("Fiction books").build();

    var response = mapper.toRest(genre);

    assertEquals(id, response.getId());
    assertEquals("Fiction", response.getName());
    assertEquals("Fiction books", response.getDescription());
  }

  @Test
  void toResponse_maps_null_description() {
    var genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").description(null).build();

    var response = mapper.toRest(genre);

    assertNull(response.getDescription());
  }

  @Test
  void toEntity_creates_genre_from_request() {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();

    var genre = mapper.toDomain(request);

    assertNull(genre.getId());
    assertEquals("Fiction", genre.getName());
    assertEquals("Fiction books", genre.getDescription());
  }

  @Test
  void toEntity_creates_genre_with_null_description() {
    var request = CreateGenreRequest.builder().name("Fiction").build();

    var genre = mapper.toDomain(request);

    assertEquals("Fiction", genre.getName());
    assertNull(genre.getDescription());
  }

  @Test
  void toBookSummaryResponse_maps_all_fields() {
    var bookId = UUID.randomUUID();
    var authorId = UUID.randomUUID();
    var genreId = UUID.randomUUID();
    var now = Instant.now();

    var genre = Genre.builder().id(genreId).name("Fiction").build();
    var author = Author.builder().id(authorId).firstName("John").lastName("Doe").build();

    var book =
        Book.builder()
            .id(bookId)
            .title("Test Book")
            .language(BookLanguage.ENGLISH)
            .coverUrl("http://cover.url")
            .createdAt(now)
            .genres(Set.of(genre))
            .build();

    var bookAuthor =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .book(book)
            .author(author)
            .role(AuthorRole.AUTHOR)
            .build();
    book.setBookAuthors(List.of(bookAuthor));

    var response = mapper.toBookSummaryResponse(book);

    assertEquals(bookId, response.getId());
    assertEquals("Test Book", response.getTitle());
    assertEquals("ENGLISH", response.getLanguage());
    assertEquals("http://cover.url", response.getCoverUrl());
    assertEquals(now, response.getCreatedAt());
    assertEquals(List.of("John Doe"), response.getAuthorNames());
    assertEquals(List.of("Fiction"), response.getGenreNames());
  }

  @Test
  void toBookSummaryResponse_maps_null_language() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Test")
            .language(null)
            .createdAt(Instant.now())
            .build();

    var response = mapper.toBookSummaryResponse(book);

    assertNull(response.getLanguage());
  }

  @Test
  void toBookSummaryResponse_handles_empty_authors_and_genres() {
    var book = Book.builder().id(UUID.randomUUID()).title("Test").createdAt(Instant.now()).build();

    var response = mapper.toBookSummaryResponse(book);

    assertTrue(response.getAuthorNames().isEmpty());
    assertTrue(response.getGenreNames().isEmpty());
  }

  @Test
  void toRevenuePerGenreResponse_maps_row() {
    var row = new Object[] {"Fiction", 500.0};

    var response = mapper.toRevenuePerGenreResponse(row);

    assertEquals("Fiction", response.getGenreName());
    assertEquals(BigDecimal.valueOf(500.0), response.getRevenue());
  }

  @Test
  void toRevenuePerGenreResponse_handles_null_revenue() {
    var row = new Object[] {"New Genre", null};

    var response = mapper.toRevenuePerGenreResponse(row);

    assertEquals("New Genre", response.getGenreName());
    assertEquals(BigDecimal.ZERO, response.getRevenue());
  }
}
