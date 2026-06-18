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
import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
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

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthorService authorService;

  @Test
  void createAuthor_should_return_created() throws Exception {
    var request = new CreateAuthorRequest().firstName("Jane").lastName("Austen");
    var response =
        new AuthorResponse()
            .id(UUID.randomUUID())
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen")
            .createdAt(Instant.now());

    given(authorService.createAuthor(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Austen"))
        .andExpect(jsonPath("$.fullName").value("Jane Austen"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void createAuthor_should_return_400_when_firstName_missing() throws Exception {
    var request = new CreateAuthorRequest().lastName("Austen");

    mockMvc
        .perform(
            post("/api/v1/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createAuthor_should_return_400_when_lastName_missing() throws Exception {
    var request = new CreateAuthorRequest().firstName("Jane");

    mockMvc
        .perform(
            post("/api/v1/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAllAuthors_should_return_page() throws Exception {
    var response =
        new AuthorResponse()
            .id(UUID.randomUUID())
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen");

    Page<AuthorResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

    given(authorService.getAllAuthors(any())).willReturn(page);

    mockMvc
        .perform(get("/api/v1/authors").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
        .andExpect(jsonPath("$.content[0].lastName").value("Austen"))
        .andExpect(jsonPath("$.content[0].fullName").value("Jane Austen"))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void getById_should_return_author() throws Exception {
    var id = UUID.randomUUID().toString();
    var response =
        new AuthorResponse()
            .id(UUID.fromString(id))
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen");

    given(authorService.getById(id)).willReturn(response);

    mockMvc
        .perform(get("/api/v1/authors/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Austen"));
  }

  @Test
  void update_should_return_updated_author() throws Exception {
    var id = UUID.randomUUID().toString();
    var request = new UpdateAuthorRequest().firstName("Emily").lastName("Bronte");
    var response =
        new AuthorResponse()
            .id(UUID.fromString(id))
            .firstName("Emily")
            .lastName("Bronte")
            .fullName("Emily Bronte");

    given(authorService.update(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/api/v1/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Emily"))
        .andExpect(jsonPath("$.lastName").value("Bronte"));
  }

  @Test
  void delete_should_return_no_content() throws Exception {
    var id = UUID.randomUUID().toString();
    willDoNothing().given(authorService).deleteById(id);

    mockMvc.perform(delete("/api/v1/authors/{id}", id)).andExpect(status().isNoContent());
  }
}
