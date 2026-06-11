package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.conf.TestUtils;
import com.onlydevs.bookstore.endpoint.rest.api.AuthorsApi;
import com.onlydevs.bookstore.endpoint.rest.client.ApiException;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

class AuthorIT extends FacadeIT {

  @LocalServerPort private int port;

  @Test
  void createAuthor_should_persist_and_return_author() throws ApiException {
    var api = new AuthorsApi(TestUtils.createApiClient(port));
    var request = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var result = api.authorsPost(request);

    assertNotNull(result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
    assertEquals("Jane Austen", result.getFullName());
    assertNotNull(result.getCreatedAt());
  }

  @Test
  void findById_should_return_author() throws ApiException {
    var api = new AuthorsApi(TestUtils.createApiClient(port));
    var request = new CreateAuthorRequest().firstName("Jane").lastName("Austen");

    var created = api.authorsPost(request);
    var result = api.authorsIdGet(created.getId());

    assertEquals(created.getId(), result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Austen", result.getLastName());
  }
}
