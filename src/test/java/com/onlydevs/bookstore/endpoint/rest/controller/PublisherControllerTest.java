package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.request.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.PublisherService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublisherController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublisherControllerTest {

  @MockBean private PublisherService publisherService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @Test
  void should_create_publisher_ok() throws Exception {
    var request =
        CreatePublisherRequest.builder().name("New Publisher").email("new@example.com").build();
    var response = PublisherResponse.builder().id(UUID.randomUUID()).name("New Publisher").build();

    given(publisherService.createPublisher(any(CreatePublisherRequest.class))).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Publisher"));
  }

  @Test
  void should_fail_when_duplicate_email() throws Exception {
    var request =
        CreatePublisherRequest.builder().name("Test").email("duplicate@example.com").build();

    given(publisherService.createPublisher(any(CreatePublisherRequest.class)))
        .willThrow(new ConflictException("Email already exists"));

    mockMvc
        .perform(
            post("/api/v1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void should_get_all_publishers_ok() throws Exception {
    var publisher =
        PublisherResponse.builder().id(UUID.randomUUID()).name("Test Publisher").build();

    given(publisherService.getAllPublishers(any())).willReturn(new PageImpl<>(List.of(publisher)));

    mockMvc
        .perform(get("/api/v1/publishers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Test Publisher"));
  }

  @Test
  void should_get_publisher_by_id_ok() throws Exception {
    var id = UUID.randomUUID();
    var response = PublisherResponse.builder().id(id).name("Test Publisher").build();

    given(publisherService.getPublisherById(id)).willReturn(response);

    mockMvc
        .perform(get("/api/v1/publishers/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Publisher"));
  }

  @Test
  void should_fail_when_publisher_not_found() throws Exception {
    var id = UUID.randomUUID();

    given(publisherService.getPublisherById(id))
        .willThrow(new NotFoundException("Publisher not found"));

    mockMvc.perform(get("/api/v1/publishers/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void should_update_publisher_ok() throws Exception {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().name("Updated Name").build();
    var response = PublisherResponse.builder().id(id).name("Updated Name").build();

    given(publisherService.updatePublisher(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/api/v1/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  void should_fail_when_update_not_found() throws Exception {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().name("New Name").build();

    given(publisherService.updatePublisher(any(), any()))
        .willThrow(new NotFoundException("Publisher not found"));

    mockMvc
        .perform(
            put("/api/v1/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_delete_publisher_ok() throws Exception {
    mockMvc
        .perform(delete("/api/v1/publishers/{id}", UUID.randomUUID()))
        .andExpect(status().isNoContent());
  }

  @Test
  void should_fail_when_delete_not_found() throws Exception {
    var id = UUID.randomUUID();

    willThrow(new NotFoundException("Publisher not found"))
        .given(publisherService)
        .deletePublisher(id);

    mockMvc.perform(delete("/api/v1/publishers/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void should_fail_when_create_publisher_with_empty_body() throws Exception {
    var request = CreatePublisherRequest.builder().build();

    mockMvc
        .perform(
            post("/api/v1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_get() throws Exception {
    mockMvc
        .perform(get("/api/v1/publishers/{id}", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_update() throws Exception {
    var request = UpdatePublisherRequest.builder().name("Updated").build();

    mockMvc
        .perform(
            put("/api/v1/publishers/{id}", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_fail_when_invalid_uuid_for_delete() throws Exception {
    mockMvc
        .perform(delete("/api/v1/publishers/{id}", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}
