package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

class AuthorIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate rest;

  @Test
  void createAuthor_should_persist_and_return_author() {
    var request = new CreateAuthorRequest("Jane", "Austen");

    AuthorResponse result =
        rest.postForObject("http://localhost:" + port + "/api/v1/authors", request, AuthorResponse.class);

    assertNotNull(result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void findById_should_return_author() {
    var createRequest = new CreateAuthorRequest("Jane", "Austen");
    AuthorResponse created =
        rest.postForObject(
            "http://localhost:" + port + "/api/v1/authors", createRequest, AuthorResponse.class);

    AuthorResponse result =
        rest.getForObject(
            "http://localhost:" + port + "/api/v1/authors/" + created.getId(), AuthorResponse.class);

    assertEquals(created.getId(), result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
  }
}
