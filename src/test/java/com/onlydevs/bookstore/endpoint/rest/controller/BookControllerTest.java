package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.request.CreateBookRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookRequest;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.service.BookService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  @MockBean private InventoryMapper inventoryMapper;

  private final UUID bookId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();

  @Test
  void getAllBooks_ShouldReturnPageOfBooks() throws Exception {
    BookSummaryResponse response =
        BookSummaryResponse.builder()
            .id(bookId)
            .title("The Great Gatsby")
            .language("ENGLISH")
            .coverUrl("https://example.com/gatsby.jpg")
            .authorNames(List.of("Scott Fitzgerald"))
            .genreNames(List.of("Fiction"))
            .createdAt(Instant.now())
            .build();

    Page<BookSummaryResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

    given(bookService.getAllBooks(any())).willReturn(page);

    mockMvc
        .perform(get("/api/v1/books").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(bookId.toString()))
        .andExpect(jsonPath("$.content[0].title").value("The Great Gatsby"))
        .andExpect(jsonPath("$.content[0].authorNames[0]").value("Scott Fitzgerald"))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void getBookById_ShouldReturnBook() throws Exception {
    BookDetailResponse response =
        BookDetailResponse.builder().id(bookId).title("Test Book").build();

    given(bookService.getBookById(bookId)).willReturn(response);

    mockMvc
        .perform(get("/api/v1/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId.toString()))
        .andExpect(jsonPath("$.title").value("Test Book"));
  }

  @Test
  void createBook_ShouldReturnCreated() throws Exception {
    CreateBookRequest request =
        CreateBookRequest.builder().title("New Book").language("ENGLISH").build();

    BookDetailResponse response =
        BookDetailResponse.builder().id(bookId).title("New Book").language("ENGLISH").build();

    given(bookService.createBook(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("New Book"));
  }

  @Test
  void createBook_WithInvalidBody_ShouldReturnBadRequest() throws Exception {
    CreateBookRequest request = CreateBookRequest.builder().build();

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateBook_ShouldReturnOk() throws Exception {
    UpdateBookRequest request = UpdateBookRequest.builder().title("Updated Title").build();

    BookDetailResponse response =
        BookDetailResponse.builder().id(bookId).title("Updated Title").build();

    given(bookService.updateBook(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/api/v1/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"));
  }

  @Test
  void deleteBook_ShouldReturnNoContent() throws Exception {
    willDoNothing().given(bookService).deleteBook(bookId);

    mockMvc.perform(delete("/api/v1/books/{id}", bookId)).andExpect(status().isNoContent());
  }

  @Test
  void addAuthorToBook_ShouldReturnCreated() throws Exception {
    UUID authorId = UUID.randomUUID();
    BookAuthorResponse response =
        BookAuthorResponse.builder()
            .id(UUID.randomUUID())
            .bookId(bookId)
            .authorId(authorId)
            .build();

    given(bookService.addAuthorToBook(bookId, authorId)).willReturn(response);

    mockMvc
        .perform(post("/api/v1/books/{id}/authors/{authorId}", bookId, authorId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.authorId").value(authorId.toString()));
  }

  @Test
  void removeAuthorFromBook_ShouldReturnNoContent() throws Exception {
    UUID authorId = UUID.randomUUID();
    willDoNothing().given(bookService).removeAuthorFromBook(bookId, authorId);

    mockMvc
        .perform(delete("/api/v1/books/{id}/authors/{authorId}", bookId, authorId))
        .andExpect(status().isNoContent());
  }

  @Test
  void addGenreToBook_ShouldReturnNoContent() throws Exception {
    UUID genreId = UUID.randomUUID();
    willDoNothing().given(bookService).addGenreToBook(bookId, genreId);

    mockMvc
        .perform(post("/api/v1/books/{id}/genres/{genreId}", bookId, genreId))
        .andExpect(status().isNoContent());
  }

  @Test
  void removeGenreFromBook_ShouldReturnNoContent() throws Exception {
    UUID genreId = UUID.randomUUID();
    willDoNothing().given(bookService).removeGenreFromBook(bookId, genreId);

    mockMvc
        .perform(delete("/api/v1/books/{id}/genres/{genreId}", bookId, genreId))
        .andExpect(status().isNoContent());
  }

  @Test
  void getEditionStock_ShouldReturnStock() throws Exception {
    given(bookService.getEditionStock(bookId, editionId)).willReturn(42);

    mockMvc
        .perform(get("/api/v1/books/{bookId}/editions/{editionId}/stock", bookId, editionId))
        .andExpect(status().isOk())
        .andExpect(content().string("42"));
  }

  @Test
  void getBookTotalStock_ShouldReturnStock() throws Exception {
    given(bookService.getBookTotalStock(bookId)).willReturn(100);

    mockMvc
        .perform(get("/api/v1/books/{bookId}/stock", bookId))
        .andExpect(status().isOk())
        .andExpect(content().string("100"));
  }

  @Test
  void getBookLowStock_ShouldReturnItems() throws Exception {
    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .editionId(editionId)
            .quantityOnHand(2)
            .reorderLevel(5)
            .lowStock(true)
            .build();

    given(bookService.getBookLowStock(bookId)).willReturn(List.of());
    given(inventoryMapper.toRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/books/{bookId}/low-stock", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].lowStock").value(true));
  }
}
