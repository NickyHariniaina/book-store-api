package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.BookEditionService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookEditionController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookEditionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookEditionService bookEditionService;

  private final UUID bookId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();
  private final UUID publisherId = UUID.randomUUID();

  private BookEditionResponse createResponse() {
    PublisherResponse publisher =
        PublisherResponse.builder().id(publisherId).name("Test Publisher").build();

    return BookEditionResponse.builder()
        .id(editionId)
        .bookId(bookId)
        .bookTitle("Test Book")
        .publisher(publisher)
        .isbn("9783161484100")
        .edition("1st")
        .format("PAPERBACK")
        .active(true)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  @Test
  void getEditionsByBookId_ShouldReturnOk() throws Exception {
    BookEditionResponse response = createResponse();
    given(bookEditionService.getEditionsByBookId(bookId)).willReturn(List.of(response));

    mockMvc
        .perform(get("/books/{bookId}/editions", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(editionId.toString()))
        .andExpect(jsonPath("$[0].isbn").value("9783161484100"));
  }

  @Test
  void getEditionById_ShouldReturnOk() throws Exception {
    BookEditionResponse response = createResponse();
    given(bookEditionService.getEditionById(editionId)).willReturn(response);

    mockMvc
        .perform(get("/editions/{id}", editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(editionId.toString()))
        .andExpect(jsonPath("$.isbn").value("9783161484100"));
  }

  @Test
  void getEditionById_WhenNotFound_ShouldReturn404() throws Exception {
    given(bookEditionService.getEditionById(editionId))
        .willThrow(new NotFoundException("Edition not found"));

    mockMvc.perform(get("/editions/{id}", editionId)).andExpect(status().isNotFound());
  }

  @Test
  void getEditionByIsbn_ShouldReturnOk() throws Exception {
    BookEditionResponse response = createResponse();
    given(bookEditionService.getEditionByIsbn("9783161484100")).willReturn(response);

    mockMvc
        .perform(get("/editions/isbn/{isbn}", "9783161484100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value("9783161484100"));
  }

  @Test
  void createEdition_ShouldReturnCreated() throws Exception {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder()
            .publisherId(publisherId)
            .isbn("9783161484100")
            .edition("1st")
            .format("PAPERBACK")
            .build();

    BookEditionResponse response = createResponse();
    given(bookEditionService.createEdition(any(), any())).willReturn(response);

    mockMvc
        .perform(
            post("/books/{bookId}/editions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(editionId.toString()));
  }

  @Test
  void createEdition_WithInvalidBody_ShouldReturnBadRequest() throws Exception {
    CreateBookEditionRequest request = CreateBookEditionRequest.builder().build();

    mockMvc
        .perform(
            post("/books/{bookId}/editions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateEdition_ShouldReturnOk() throws Exception {
    UpdateBookEditionRequest request =
        UpdateBookEditionRequest.builder()
            .isbn("9783161484101")
            .edition("2nd")
            .format("HARDCOVER")
            .build();

    BookEditionResponse response = createResponse();
    given(bookEditionService.updateEdition(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/editions/{id}", editionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(editionId.toString()));
  }

  @Test
  void activateEdition_ShouldReturnOk() throws Exception {
    BookEditionResponse response = createResponse();
    given(bookEditionService.activateEdition(editionId)).willReturn(response);

    mockMvc
        .perform(patch("/editions/{id}/activate", editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(editionId.toString()));
  }

  @Test
  void deactivateEdition_ShouldReturnOk() throws Exception {
    BookEditionResponse response = createResponse();
    given(bookEditionService.deactivateEdition(editionId)).willReturn(response);

    mockMvc
        .perform(patch("/editions/{id}/deactivate", editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(editionId.toString()));
  }
}
