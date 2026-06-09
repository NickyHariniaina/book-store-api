package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
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
}
