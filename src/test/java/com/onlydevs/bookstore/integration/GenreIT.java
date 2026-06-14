package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.endpoint.rest.model.CreateGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.GenreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.RenameGenreRequest;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.repository.GenreRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GenreIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private GenreRepository genreRepository;

  private String baseUri;

  @BeforeEach
  void setup() {
    baseUri = "http://localhost:" + port + "/genres";
    genreRepository.deleteAll();
  }

  @Test
  void should_create_genre_ok() {
    var request = new CreateGenreRequest().name("Fiction").description("Fiction books");

    ResponseEntity<GenreResponse> response =
        restTemplate.postForEntity(baseUri, request, GenreResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals("Fiction", response.getBody().getName());
  }

  @Test
  void should_fail_when_duplicate_name() {
    genreRepository.save(Genre.builder().name("Fiction").build());

    var request = new CreateGenreRequest().name("Fiction");

    var response = restTemplate.postForEntity(baseUri, request, String.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void should_get_all_genres_ok() {
    genreRepository.save(Genre.builder().name("Science").build());
    genreRepository.save(Genre.builder().name("Fiction").build());

    var response = restTemplate.getForEntity(baseUri, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Fiction"));
    assertTrue(response.getBody().contains("Science"));
  }

  @Test
  void should_rename_genre_ok() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    var request = new RenameGenreRequest().name("Science");

    var response =
        restTemplate.exchange(
            baseUri + "/" + saved.getId() + "/rename",
            HttpMethod.PATCH,
            new org.springframework.http.HttpEntity<>(request),
            GenreResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Science", response.getBody().getName());
  }

  @Test
  void should_fail_when_rename_not_found() {
    var request = new RenameGenreRequest().name("Science");

    var response =
        restTemplate.exchange(
            baseUri + "/" + UUID.randomUUID() + "/rename",
            HttpMethod.PATCH,
            new org.springframework.http.HttpEntity<>(request),
            String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_fail_when_rename_duplicate() {
    genreRepository.save(Genre.builder().name("Science").build());
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    var request = new RenameGenreRequest().name("Science");

    var response =
        restTemplate.exchange(
            baseUri + "/" + saved.getId() + "/rename",
            HttpMethod.PATCH,
            new org.springframework.http.HttpEntity<>(request),
            String.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void should_delete_genre_ok() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    restTemplate.delete(baseUri + "/" + saved.getId());

    var response = restTemplate.getForEntity(baseUri + "/" + saved.getId(), String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_fail_when_delete_not_found() {
    var response =
        restTemplate.exchange(
            baseUri + "/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_get_books_by_genre_ok_when_empty() {
    var saved = genreRepository.save(Genre.builder().name("Fiction").build());

    var response = restTemplate.getForEntity(baseUri + "/" + saved.getId() + "/books", String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void should_get_books_by_genre_fail_when_not_found() {
    var response = restTemplate.getForEntity(baseUri + "/" + UUID.randomUUID() + "/books", String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_get_revenue_per_genre_ok() {
    var response = restTemplate.getForEntity(baseUri + "/revenue", String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
