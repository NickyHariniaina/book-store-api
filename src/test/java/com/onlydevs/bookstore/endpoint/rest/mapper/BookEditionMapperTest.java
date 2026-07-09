package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.model.enums.BookFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookEditionMapperTest {

  private final BookEditionMapper mapper = new BookEditionMapper();

  @Test
  void toBookEditionResponse_should_map_all_fields() {
    var bookId = UUID.randomUUID();
    var publisherId = UUID.randomUUID();
    var editionId = UUID.randomUUID();
    var now = Instant.now();
    var publisher =
        Publisher.builder()
            .id(publisherId)
            .name("cute Publisher")
            .website("https://cute.cute")
            .email("cute@cute.com")
            .phone("1234567890")
            .country("US")
            .createdAt(now)
            .build();
    var book = Book.builder().id(bookId).title("cute book").build();
    var edition =
        BookEdition.builder()
            .id(editionId)
            .book(book)
            .publisher(publisher)
            .isbn("9783161484100")
            .edition("1st")
            .format(BookFormat.PAPERBACK)
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    BookEditionResponse result = mapper.toBookEditionResponse(edition);

    assertEquals(editionId, result.getId());
    assertEquals(bookId, result.getBookId());
    assertEquals("cute book", result.getBookTitle());
    assertNotNull(result.getPublisher());
    assertEquals(publisherId, result.getPublisher().getId());
    assertEquals("cute Publisher", result.getPublisher().getName());
    assertEquals("9783161484100", result.getIsbn());
    assertEquals("1st", result.getEdition());
    assertEquals("PAPERBACK", result.getFormat());
    assertTrue(result.isActive());
    assertEquals(now, result.getCreatedAt());
    assertEquals(now, result.getUpdatedAt());
  }

  @Test
  void toBookEditionResponse_should_return_null_when_edition_is_null() {
    assertNull(mapper.toBookEditionResponse(null));
  }

  @Test
  void toBookEditionResponse_should_map_null_publisher() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute book").build();
    var edition =
        BookEdition.builder()
            .id(UUID.randomUUID())
            .book(book)
            .publisher(null)
            .isbn("9783161484100")
            .format(BookFormat.HARDCOVER)
            .active(true)
            .createdAt(Instant.now())
            .build();

    BookEditionResponse result = mapper.toBookEditionResponse(edition);

    assertNull(result.getPublisher());
  }

  @Test
  void toBookEditionResponse_should_map_inactive_edition() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute book").build();
    var edition =
        BookEdition.builder()
            .id(UUID.randomUUID())
            .book(book)
            .isbn("9783161484100")
            .format(BookFormat.PAPERBACK)
            .active(false)
            .createdAt(Instant.now())
            .build();

    BookEditionResponse result = mapper.toBookEditionResponse(edition);

    assertFalse(result.isActive());
  }

  @Test
  void toBookEditionResponseList_should_map_list() {
    var book = Book.builder().id(UUID.randomUUID()).title("cute book").build();
    var edition1 =
        BookEdition.builder()
            .id(UUID.randomUUID())
            .book(book)
            .isbn("9783161484100")
            .format(BookFormat.PAPERBACK)
            .build();
    var edition2 =
        BookEdition.builder()
            .id(UUID.randomUUID())
            .book(book)
            .isbn("9783161484101")
            .format(BookFormat.HARDCOVER)
            .build();
    var editions = List.of(edition1, edition2);

    var result = mapper.toBookEditionResponseList(editions);

    assertEquals(2, result.size());
    assertEquals("9783161484100", result.get(0).getIsbn());
    assertEquals("9783161484101", result.get(1).getIsbn());
  }

  @Test
  void toBookEditionResponseList_should_return_empty_when_list_is_empty() {
    var result = mapper.toBookEditionResponseList(List.of());

    assertTrue(result.isEmpty());
  }
}
