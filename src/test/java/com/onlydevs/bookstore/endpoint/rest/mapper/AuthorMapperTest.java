package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthorMapperTest {

  private final AuthorMapper authorMapper = new AuthorMapper();

  @Test
  void toDomain_should_map_request_to_author() {
    CreateAuthorRequest request =
        CreateAuthorRequest.builder().firstName("John").lastName("Doe").build();

    Author result = authorMapper.toDomain(request);

    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertNull(result.getId());
    assertNull(result.getCreatedAt());
    assertTrue(result.getBookAuthors().isEmpty());
  }

  @Test
  void toRest_should_map_author_to_response() {
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    Author author =
        Author.builder().id(id).firstName("Jane").lastName("Austen").createdAt(now).build();

    AuthorResponse result = authorMapper.toRest(author);

    assertEquals(id, result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertEquals(now, result.getCreatedAt());
  }
}
