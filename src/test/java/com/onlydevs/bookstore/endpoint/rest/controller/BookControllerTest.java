package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreateBookRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookRequest;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.service.BookService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  private final UUID bookId = UUID.randomUUID();

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

    when(bookService.getAllBooks(any())).thenReturn(page);

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

    when(bookService.getBookById(bookId)).thenReturn(response);

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

    when(bookService.createBook(any())).thenReturn(response);

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

    when(bookService.updateBook(any(), any())).thenReturn(response);

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
    doNothing().when(bookService).deleteBook(bookId);

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

    when(bookService.addAuthorToBook(bookId, authorId)).thenReturn(response);

    mockMvc
        .perform(post("/api/v1/books/{id}/authors/{authorId}", bookId, authorId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.authorId").value(authorId.toString()));
  }

  @Test
  void removeAuthorFromBook_ShouldReturnNoContent() throws Exception {
    UUID authorId = UUID.randomUUID();
    doNothing().when(bookService).removeAuthorFromBook(bookId, authorId);

    mockMvc
        .perform(delete("/api/v1/books/{id}/authors/{authorId}", bookId, authorId))
        .andExpect(status().isNoContent());
  }

  @Test
  void addGenreToBook_ShouldReturnNoContent() throws Exception {
    UUID genreId = UUID.randomUUID();
    doNothing().when(bookService).addGenreToBook(bookId, genreId);

    mockMvc
        .perform(post("/api/v1/books/{id}/genres/{genreId}", bookId, genreId))
        .andExpect(status().isNoContent());
  }

  @Test
  void removeGenreFromBook_ShouldReturnNoContent() throws Exception {
    UUID genreId = UUID.randomUUID();
    doNothing().when(bookService).removeGenreFromBook(bookId, genreId);

    mockMvc
        .perform(delete("/api/v1/books/{id}/genres/{genreId}", bookId, genreId))
        .andExpect(status().isNoContent());
  }
}
