package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.endpoint.rest.model.BookSummaryResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.GenreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.RenameGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.GenreService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

  @MockBean private GenreService genreService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @Test
  void should_get_all_genres_ok() throws Exception {
    var g1 = new GenreResponse().id(UUID.randomUUID()).name("Fiction");
    var g2 = new GenreResponse().id(UUID.randomUUID()).name("Science");

    given(genreService.getAllGenres()).willReturn(List.of(g1, g2));

    mockMvc
        .perform(get("/genres"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].name").value("Fiction"))
        .andExpect(jsonPath("$[1].name").value("Science"));
  }

  @Test
  void should_create_genre_ok() throws Exception {
    var request = new CreateGenreRequest().name("Fiction").description("Fiction books");
    var response =
        new GenreResponse().id(UUID.randomUUID()).name("Fiction").description("Fiction books");

    given(genreService.createGenre(any(CreateGenreRequest.class))).willReturn(response);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Fiction"));
  }

  @Test
  void should_fail_when_create_duplicate_genre() throws Exception {
    var request = new CreateGenreRequest().name("Fiction");

    given(genreService.createGenre(any(CreateGenreRequest.class)))
        .willThrow(new ConflictException("Genre already exists"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void should_rename_genre_ok() throws Exception {
    var id = UUID.randomUUID();
    var request = new RenameGenreRequest().name("Science");
    var response = new GenreResponse().id(id).name("Science");

    given(genreService.renameGenre(eq(id), any(RenameGenreRequest.class))).willReturn(response);

    mockMvc
        .perform(
            patch("/genres/{id}/rename", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Science"));
  }

  @Test
  void should_fail_when_rename_not_found() throws Exception {
    var id = UUID.randomUUID();
    var request = new RenameGenreRequest().name("Science");

    given(genreService.renameGenre(eq(id), any(RenameGenreRequest.class)))
        .willThrow(new NotFoundException("Genre not found"));

    mockMvc
        .perform(
            patch("/genres/{id}/rename", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_get_books_by_genre_ok() throws Exception {
    var id = UUID.randomUUID();
    var book = new BookSummaryResponse().id(UUID.randomUUID()).title("Test Book");
    var page = new org.springframework.data.domain.PageImpl<>(List.of(book));

    given(genreService.getBooksByGenreId(eq(id), any())).willReturn(page);

    mockMvc
        .perform(get("/genres/{id}/books", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Test Book"));
  }

  @Test
  void should_delete_genre_ok() throws Exception {
    mockMvc.perform(delete("/genres/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
  }

  @Test
  void should_fail_when_delete_not_found() throws Exception {
    var id = UUID.randomUUID();

    willThrow(new NotFoundException("Genre not found")).given(genreService).deleteGenre(id);

    mockMvc.perform(delete("/genres/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void should_get_revenue_per_genre_ok() throws Exception {
    var r1 = new RevenuePerGenreResponse().genreName("Fiction").revenue(BigDecimal.valueOf(500.0));

    given(genreService.getRevenuePerGenre()).willReturn(List.of(r1));

    mockMvc
        .perform(get("/genres/revenue"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].genreName").value("Fiction"))
        .andExpect(jsonPath("$[0].revenue").value(500.0));
  }
}
