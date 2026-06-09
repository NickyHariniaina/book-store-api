package com.onlydevs.bookstore.model.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenreDtoTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void createGenreRequest_valid_when_name_provided() {
    CreateGenreRequest request =
        CreateGenreRequest.builder().name("Fiction").description("Desc").build();
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  void createGenreRequest_invalid_when_name_blank() {
    CreateGenreRequest request = CreateGenreRequest.builder().name("").build();
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void createGenreRequest_valid_when_description_null() {
    CreateGenreRequest request = CreateGenreRequest.builder().name("Fiction").build();
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  void renameGenreRequest_valid_when_name_provided() {
    RenameGenreRequest request = RenameGenreRequest.builder().name("New Name").build();
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  void renameGenreRequest_invalid_when_name_blank() {
    RenameGenreRequest request = RenameGenreRequest.builder().name("").build();
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void genreResponse_builds_correctly() {
    java.util.UUID id = java.util.UUID.randomUUID();
    GenreResponse response =
        GenreResponse.builder().id(id).name("Fiction").description("Desc").build();
    assertEquals(id, response.id);
    assertEquals("Fiction", response.name);
    assertEquals("Desc", response.description);
  }

  @Test
  void revenuePerGenreResponse_builds_correctly() {
    RevenuePerGenreResponse response =
        RevenuePerGenreResponse.builder().genreName("Fiction").revenue(500.0).build();
    assertEquals("Fiction", response.genreName);
    assertEquals(500.0, response.revenue);
  }

  @Test
  void bookSummaryResponse_builds_correctly() {
    java.util.UUID id = java.util.UUID.randomUUID();
    java.time.Instant now = java.time.Instant.now();
    BookSummaryResponse response =
        BookSummaryResponse.builder()
            .id(id)
            .title("Test")
            .language("ENGLISH")
            .coverUrl("http://url")
            .authorNames(java.util.List.of("John Doe"))
            .genreNames(java.util.List.of("Fiction"))
            .createdAt(now)
            .build();
    assertEquals(id, response.id);
    assertEquals("Test", response.title);
    assertEquals("ENGLISH", response.language);
    assertEquals(1, response.authorNames.size());
    assertEquals(1, response.genreNames.size());
  }
}
