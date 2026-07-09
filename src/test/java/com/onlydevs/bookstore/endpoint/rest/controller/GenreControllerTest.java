package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.request.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.GenreService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
class GenreControllerTest {

  @MockBean private GenreService genreService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @Test
  void should_get_all_genres_ok() throws Exception {
    var g1 = GenreResponse.builder().id(UUID.randomUUID()).name("Fiction").build();
    var g2 = GenreResponse.builder().id(UUID.randomUUID()).name("Science").build();
    var page = new PageImpl<>(List.of(g1, g2));

    given(genreService.getAllGenres(any(Pageable.class))).willReturn(page);

    mockMvc
        .perform(get("/genres"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(2))
        .andExpect(jsonPath("$.content[0].name").value("Fiction"))
        .andExpect(jsonPath("$.content[1].name").value("Science"))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void should_create_genre_ok() throws Exception {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();
    var response =
        GenreResponse.builder()
            .id(UUID.randomUUID())
            .name("Fiction")
            .description("Fiction books")
            .build();

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
    var request = CreateGenreRequest.builder().name("Fiction").build();

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
    var request = RenameGenreRequest.builder().name("Science").build();
    var response = GenreResponse.builder().id(id).name("Science").build();

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
    var request = RenameGenreRequest.builder().name("Science").build();

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
    var book = BookSummaryResponse.builder().id(UUID.randomUUID()).title("Test Book").build();
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
    var r1 =
        RevenuePerGenreResponse.builder()
            .genreName("Fiction")
            .revenue(BigDecimal.valueOf(500.0))
            .build();

    given(genreService.getRevenuePerGenre()).willReturn(List.of(r1));

    mockMvc
        .perform(get("/genres/revenue"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].genreName").value("Fiction"))
        .andExpect(jsonPath("$[0].revenue").value(500.0));
  }

  @Test
  void should_fail_when_create_genre_with_empty_body() throws Exception {
    var request = CreateGenreRequest.builder().build();

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_rename() throws Exception {
    var request = RenameGenreRequest.builder().name("Science").build();

    mockMvc
        .perform(
            patch("/genres/{id}/rename", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_delete() throws Exception {
    mockMvc.perform(delete("/genres/{id}", "invalid-uuid")).andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_get_books() throws Exception {
    mockMvc.perform(get("/genres/{id}/books", "invalid-uuid")).andExpect(status().isBadRequest());
  }
}
