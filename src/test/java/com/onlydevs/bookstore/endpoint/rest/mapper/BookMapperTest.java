package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookAuthor;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.enums.AuthorRole;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  private final GenreMapper genreMapper = new GenreMapper();
  private final BookMapper bookMapper = new BookMapper(genreMapper);

  @Test
  void toBookSummaryResponse_should_map_all_fields() {
    var author =
        Author.builder().id(UUID.randomUUID()).firstName("Tafita").lastName("Mata").build();
    var genre = Genre.builder().id(UUID.randomUUID()).name("cute").build();
    var bookAuthor =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute story")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/cute.jpg")
            .createdAt(Instant.now())
            .bookAuthors(Set.of(bookAuthor))
            .genres(Set.of(genre))
            .build();
    bookAuthor.setBook(book);

    BookSummaryResponse result = bookMapper.toBookSummaryResponse(book);

    assertEquals(book.getId(), result.getId());
    assertEquals("cute story", result.getTitle());
    assertEquals("ENGLISH", result.getLanguage());
    assertEquals("https://example.com/cute.jpg", result.getCoverUrl());
    assertEquals(List.of("Tafita Mata"), result.getAuthorNames());
    assertEquals(List.of("cute"), result.getGenreNames());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void toBookSummaryResponse_should_return_null_when_book_is_null() {
    assertNull(bookMapper.toBookSummaryResponse(null));
  }

  @Test
  void toBookSummaryResponse_should_map_null_language() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .language(null)
            .createdAt(Instant.now())
            .build();

    BookSummaryResponse result = bookMapper.toBookSummaryResponse(book);

    assertNull(result.getLanguage());
  }

  @Test
  void toBookSummaryResponse_should_map_null_cover_url() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .language(BookLanguage.ENGLISH)
            .coverUrl(null)
            .createdAt(Instant.now())
            .build();

    BookSummaryResponse result = bookMapper.toBookSummaryResponse(book);

    assertNull(result.getCoverUrl());
  }

  @Test
  void toBookSummaryResponse_should_handle_empty_authors_and_genres() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .createdAt(Instant.now())
            .bookAuthors(Set.of())
            .genres(Set.of())
            .build();

    BookSummaryResponse result = bookMapper.toBookSummaryResponse(book);

    assertTrue(result.getAuthorNames().isEmpty());
    assertTrue(result.getGenreNames().isEmpty());
  }

  @Test
  void toBookSummaryResponse_should_sort_authors_by_contribution_order() {
    var author1 =
        Author.builder().id(UUID.randomUUID()).firstName("Tafita").lastName("Mata").build();
    var author2 =
        Author.builder().id(UUID.randomUUID()).firstName("Mata").lastName("Tafita").build();
    var ba1 =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author2)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(2)
            .build();
    var ba2 =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author1)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute book")
            .createdAt(Instant.now())
            .bookAuthors(Set.of(ba1, ba2))
            .build();
    ba1.setBook(book);
    ba2.setBook(book);

    BookSummaryResponse result = bookMapper.toBookSummaryResponse(book);

    assertEquals(List.of("Tafita Mata", "Mata Tafita"), result.getAuthorNames());
  }

  @Test
  void toBookDetailResponse_should_map_all_fields() {
    var author =
        Author.builder().id(UUID.randomUUID()).firstName("Tafita").lastName("Mata").build();
    var genre = Genre.builder().id(UUID.randomUUID()).name("cute").build();
    var bookAuthor =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();
    var now = Instant.now();
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute story")
            .summary("A cute test")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/cute.jpg")
            .createdAt(now)
            .updatedAt(now)
            .bookAuthors(Set.of(bookAuthor))
            .genres(Set.of(genre))
            .build();
    bookAuthor.setBook(book);

    BookDetailResponse result = bookMapper.toBookDetailResponse(book);

    assertEquals(book.getId(), result.getId());
    assertEquals("cute story", result.getTitle());
    assertEquals("A cute test", result.getSummary());
    assertEquals("ENGLISH", result.getLanguage());
    assertEquals("https://example.com/cute.jpg", result.getCoverUrl());
    assertEquals(1, result.getAuthors().size());
    assertEquals(1, result.getGenres().size());
    assertEquals(now, result.getCreatedAt());
    assertEquals(now, result.getUpdatedAt());
  }

  @Test
  void toBookDetailResponse_should_return_null_when_book_is_null() {
    assertNull(bookMapper.toBookDetailResponse(null));
  }

  @Test
  void toBookDetailResponse_should_map_null_language() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .summary("Summary")
            .language(null)
            .createdAt(Instant.now())
            .build();

    BookDetailResponse result = bookMapper.toBookDetailResponse(book);

    assertNull(result.getLanguage());
  }

  @Test
  void toBookAuthorResponse_should_map_all_fields() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute book").build();
    var author =
        Author.builder().id(UUID.randomUUID()).firstName("Tafita").lastName("Mata").build();
    var bookAuthor =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .book(book)
            .author(author)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();

    BookAuthorResponse result = bookMapper.toBookAuthorResponse(bookAuthor);

    assertEquals(bookAuthor.getId(), result.getId());
    assertEquals(author.getId(), result.getAuthorId());
    assertEquals("Tafita Mata", result.getAuthorFullName());
    assertEquals(AuthorRole.AUTHOR, result.getRole());
    assertEquals(1, result.getContributionOrder());
  }

  @Test
  void toBookAuthorResponseList_should_return_empty_when_authors_null() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute").createdAt(Instant.now()).build();

    var result = bookMapper.toBookAuthorResponseList(book);

    assertTrue(result.isEmpty());
  }

  @Test
  void toBookAuthorResponseList_should_return_empty_when_authors_empty() {
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .createdAt(Instant.now())
            .bookAuthors(Set.of())
            .build();

    var result = bookMapper.toBookAuthorResponseList(book);

    assertTrue(result.isEmpty());
  }

  @Test
  void toGenreResponseList_should_return_empty_when_genres_null() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute").createdAt(Instant.now()).build();

    var result = bookMapper.toGenreResponseList(book);

    assertTrue(result.isEmpty());
  }

  @Test
  void toGenreResponseList_should_map_all_genres() {
    var genre1 = Genre.builder().id(UUID.randomUUID()).name("cute").build();
    var genre2 = Genre.builder().id(UUID.randomUUID()).name("cute too").build();
    var book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("cute")
            .createdAt(Instant.now())
            .genres(Set.of(genre1, genre2))
            .build();

    var result = bookMapper.toGenreResponseList(book);

    assertEquals(2, result.size());
  }
}
