package com.onlydevs.bookstore.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookAuthor;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.CreateGenreRequest;
import com.onlydevs.bookstore.model.enums.AuthorRole;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.model.mapper.GenreMapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenreMapperTest {

  private GenreMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new GenreMapper();
  }

  @Test
  void toResponse_maps_all_fields() {
    var id = UUID.randomUUID();
    var genre = Genre.builder().id(id).name("Fiction").description("Fiction books").build();

    var response = mapper.toRest(genre);

    assertEquals(id, response.id);
    assertEquals("Fiction", response.name);
    assertEquals("Fiction books", response.description);
  }

  @Test
  void toResponse_maps_null_description() {
    var genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").description(null).build();

    var response = mapper.toRest(genre);

    assertNull(response.description);
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

    assertEquals(bookId, response.id);
    assertEquals("Test Book", response.title);
    assertEquals("ENGLISH", response.language);
    assertEquals("http://cover.url", response.coverUrl);
    assertEquals(now, response.createdAt);
    assertEquals(List.of("John Doe"), response.authorNames);
    assertEquals(List.of("Fiction"), response.genreNames);
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

    assertNull(response.language);
  }

  @Test
  void toBookSummaryResponse_handles_empty_authors_and_genres() {
    var book = Book.builder().id(UUID.randomUUID()).title("Test").createdAt(Instant.now()).build();

    var response = mapper.toBookSummaryResponse(book);

    assertTrue(response.authorNames.isEmpty());
    assertTrue(response.genreNames.isEmpty());
  }

  @Test
  void toRevenuePerGenreResponse_maps_row() {
    var row = new Object[] {"Fiction", 500.0};

    var response = mapper.toRevenuePerGenreResponse(row);

    assertEquals("Fiction", response.genreName);
    assertEquals(500.0, response.revenue);
  }

  @Test
  void toRevenuePerGenreResponse_handles_null_revenue() {
    var row = new Object[] {"New Genre", null};

    var response = mapper.toRevenuePerGenreResponse(row);

    assertEquals("New Genre", response.genreName);
    assertEquals(0.0, response.revenue);
  }
}
