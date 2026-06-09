package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.GenreResponse;
import com.onlydevs.bookstore.model.dto.RenameGenreRequest;
import com.onlydevs.bookstore.model.dto.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.GenreService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final Instant now = Instant.now();

  @Test
  void should_list_all_genres_without_pagination() throws Exception {
    var g1 =
        GenreResponse.builder()
            .id(UUID.randomUUID())
            .name("Fiction")
            .description("Fiction")
            .build();
    var g2 =
        GenreResponse.builder()
            .id(UUID.randomUUID())
            .name("Science")
            .description("Science")
            .build();

    when(genreService.getAllGenres()).thenReturn(List.of(g1, g2));

    mockMvc
        .perform(get("/genres"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].name").value("Fiction"))
        .andExpect(jsonPath("$[1].name").value("Science"));
  }

  @Test
  void should_get_genre_with_books_when_genre_exists() throws Exception {
    var pageable = PageRequest.of(0, 10);
    var book =
        BookSummaryResponse.builder()
            .id(UUID.randomUUID())
            .title("Test Book")
            .createdAt(now)
            .build();
    var page = new PageImpl<>(List.of(book), pageable, 1);

    when(genreService.getBooksByGenreId(eq(genreId), any())).thenReturn(page);

    mockMvc
        .perform(get("/genres/{id}/books", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Test Book"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void should_return_404_when_genre_not_found() throws Exception {
    when(genreService.getBooksByGenreId(eq(genreId), any()))
        .thenThrow(new NotFoundException("Genre not found with id: " + genreId));

    mockMvc.perform(get("/genres/{id}/books", genreId)).andExpect(status().isNotFound());
  }

  @Test
  void should_create_genre_and_return_201_when_valid() throws Exception {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();
    var response =
        GenreResponse.builder().id(genreId).name("Fiction").description("Fiction books").build();

    when(genreService.createGenre(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fiction"));
  }

  @Test
  void should_return_400_when_create_genre_with_empty_name() throws Exception {
    var invalidJson = "{\"name\":\"\",\"description\":\"desc\"}";

    mockMvc
        .perform(post("/genres").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_return_409_when_create_duplicate_genre() throws Exception {
    var request = CreateGenreRequest.builder().name("Fiction").description("Fiction books").build();

    when(genreService.createGenre(any()))
        .thenThrow(new ConflictException("Genre already exists: Fiction"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void should_rename_genre_when_genre_exists() throws Exception {
    var request = RenameGenreRequest.builder().name("New Name").build();
    var response = GenreResponse.builder().id(genreId).name("New Name").description("Desc").build();

    when(genreService.renameGenre(eq(genreId), any())).thenReturn(response);

    mockMvc
        .perform(
            patch("/genres/{id}/rename", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"));
  }

  @Test
  void should_return_404_when_rename_nonexistent_genre() throws Exception {
    var request = RenameGenreRequest.builder().name("New Name").build();

    when(genreService.renameGenre(eq(genreId), any()))
        .thenThrow(new NotFoundException("Genre not found with id: " + genreId));

    mockMvc
        .perform(
            patch("/genres/{id}/rename", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_return_400_when_rename_genre_to_empty_name() throws Exception {
    String invalidJson = "{\"name\":\"\"}";

    mockMvc
        .perform(
            patch("/genres/{id}/rename", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_return_409_when_rename_genre_to_existing_name() throws Exception {
    var request = RenameGenreRequest.builder().name("Taken Name").build();

    when(genreService.renameGenre(eq(genreId), any()))
        .thenThrow(new ConflictException("Genre name already taken: Taken Name"));

    mockMvc
        .perform(
            patch("/genres/{id}/rename", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void should_delete_genre_when_genre_exists() throws Exception {
    doNothing().when(genreService).deleteGenre(genreId);

    mockMvc.perform(delete("/genres/{id}", genreId)).andExpect(status().isNoContent());
  }

  @Test
  void should_return_404_when_delete_nonexistent_genre() throws Exception {
    doThrow(new NotFoundException("Genre not found with id: " + genreId))
        .when(genreService)
        .deleteGenre(genreId);

    mockMvc.perform(delete("/genres/{id}", genreId)).andExpect(status().isNotFound());
  }

  @Test
  void should_return_204_when_genre_deleted_successfully() throws Exception {
    doNothing().when(genreService).deleteGenre(genreId);

    mockMvc
        .perform(delete("/genres/{id}", genreId))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void should_get_revenue_per_genre_for_dashboard() throws Exception {
    var r1 = RevenuePerGenreResponse.builder().genreName("Fiction").revenue(500.0).build();
    var r2 = RevenuePerGenreResponse.builder().genreName("Science").revenue(300.0).build();

    when(genreService.getRevenuePerGenre()).thenReturn(List.of(r1, r2));

    mockMvc
        .perform(get("/genres/revenue"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].genreName").value("Fiction"))
        .andExpect(jsonPath("$[0].revenue").value(500.0))
        .andExpect(jsonPath("$[1].genreName").value("Science"))
        .andExpect(jsonPath("$[1].revenue").value(300.0));
  }
}
