package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.PublisherService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private PublisherService publisherService;

  @Test
  void should_list_all_publishers_with_pagination() throws Exception {
    var publisher = new PublisherResponse()
        .id(UUID.randomUUID())
        .name("Test Publisher");
    var page = new PageImpl<>(List.of(publisher));

    given(publisherService.getAllPublishers(any())).willReturn(page);

    mockMvc
        .perform(get("/publishers").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Test Publisher"));
  }

  @Test
  void should_get_publisher_by_id_when_exists() throws Exception {
    var id = UUID.randomUUID();
    var publisher = new PublisherResponse()
        .id(id)
        .name("Test Publisher");

    given(publisherService.getPublisherById(id)).willReturn(publisher);

    mockMvc
        .perform(get("/publishers/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Publisher"));
  }

  @Test
  void should_return_404_when_publisher_not_found() throws Exception {
    var id = UUID.randomUUID();

    given(publisherService.getPublisherById(id))
        .willThrow(new NotFoundException("Publisher not found with id: " + id));

    mockMvc.perform(get("/publishers/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void should_create_publisher_and_return_201_when_valid() throws Exception {
    var request =
        new CreatePublisherRequest().name("New Publisher").email("new@example.com");
    var response = new PublisherResponse()
        .id(UUID.randomUUID())
        .name("New Publisher");

    given(publisherService.createPublisher(any())).willReturn(response);

    mockMvc
        .perform(
            post("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Publisher"));
  }

  @Test
  void should_return_409_when_create_publisher_with_duplicate_email() throws Exception {
    var request =
        new CreatePublisherRequest()
            .name("Test Publisher")
            .email("duplicate@example.com");

    given(publisherService.createPublisher(any()))
        .willThrow(
            new ConflictException("Publisher with email duplicate@example.com already exists"));

    mockMvc
        .perform(
            post("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void should_update_publisher_when_exists() throws Exception {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().name("Updated Name");
    var response = new PublisherResponse()
        .id(id)
        .name("Updated Name");

    given(publisherService.updatePublisher(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  void should_return_404_when_update_nonexistent_publisher() throws Exception {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().name("New Name");

    given(publisherService.updatePublisher(any(), any()))
        .willThrow(new NotFoundException("Publisher not found with id: " + id));

    mockMvc
        .perform(
            put("/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_update_publisher_contact_info_only() throws Exception {
    var id = UUID.randomUUID();
    var request =
        new UpdatePublisherRequest().website("http://example.com").phone("1234567890");
    var response = new PublisherResponse()
        .id(id)
        .name("Existing Name")
        .website("http://example.com")
        .phone("1234567890");

    given(publisherService.updatePublisher(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.website").value("http://example.com"))
        .andExpect(jsonPath("$.phone").value("1234567890"));
  }

  @Test
  void should_delete_publisher_when_no_active_editions_linked() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/publishers/{id}", id)).andExpect(status().isNoContent());
  }

  @Test
  void should_return_404_when_delete_nonexistent_publisher() throws Exception {
    var id = UUID.randomUUID();

    willThrow(new NotFoundException("Publisher not found with id: " + id))
        .given(publisherService)
        .deletePublisher(id);

    mockMvc.perform(delete("/publishers/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void should_validate_email_uniqueness_on_update() throws Exception {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().email("taken@example.com");

    given(publisherService.updatePublisher(any(), any()))
        .willThrow(new ConflictException("Publisher with email taken@example.com already exists"));

    mockMvc
        .perform(
            put("/publishers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }
}
