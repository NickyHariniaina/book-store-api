package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.CreateAuthorResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthorMapperTest {

  private final AuthorMapper authorMapper = new AuthorMapper();

  @Test
  void toDomain_shouldMapRequestToAuthor() {
    CreateAuthorRequest request = new CreateAuthorRequest("John", "Doe");

    Author result = authorMapper.toDomain(request);

    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertNull(result.getId());
    assertNull(result.getCreatedAt());
    assertTrue(result.getBookAuthors().isEmpty());
  }

  @Test
  void toRest_shouldMapAuthorToResponse() {
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    Author author =
        Author.builder().id(id).firstName("Jane").lastName("Austen").createdAt(now).build();

    CreateAuthorResponse result = authorMapper.toRest(author);

    assertEquals(id.toString(), result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertEquals(now, result.getCreatedAt());
  }
}
