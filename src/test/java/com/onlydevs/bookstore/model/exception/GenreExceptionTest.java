package com.onlydevs.bookstore.model.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class GenreExceptionTest {

  @Test
  void notFoundException_has_correct_message() {
    UUID id = UUID.randomUUID();
    NotFoundException ex = new NotFoundException("Genre", id);
    assertTrue(ex.getMessage().contains("Genre"));
    assertTrue(ex.getMessage().contains(id.toString()));
  }

  @Test
  void notFoundException_has_not_found_status() {
    ResponseStatus status = NotFoundException.class.getAnnotation(ResponseStatus.class);
    assertNotNull(status);
    assertEquals(HttpStatus.NOT_FOUND, status.value());
  }

  @Test
  void conflictException_has_correct_message() {
    ConflictException ex = new ConflictException("Genre already exists: Fiction");
    assertEquals("Genre already exists: Fiction", ex.getMessage());
  }

  @Test
  void conflictException_has_conflict_status() {
    ResponseStatus status = ConflictException.class.getAnnotation(ResponseStatus.class);
    assertNotNull(status);
    assertEquals(HttpStatus.CONFLICT, status.value());
  }
}
