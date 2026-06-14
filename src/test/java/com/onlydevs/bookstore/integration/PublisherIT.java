package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PublisherIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private PublisherRepository publisherRepository;

  private String baseUri;

  @BeforeEach
  void setup() {
    baseUri = "http://localhost:" + port + "/publishers";
    publisherRepository.deleteAll();
  }

  @Test
  void should_create_publisher_ok() {
    var request =
        new CreatePublisherRequest().name("Test Publisher").email("test@example.com").phone("1234567890");

    ResponseEntity<PublisherResponse> response =
        restTemplate.postForEntity(baseUri, request, PublisherResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals("Test Publisher", response.getBody().getName());
  }

  @Test
  void should_fail_when_duplicate_email() {
    publisherRepository.save(
        Publisher.builder().name("Existing").phone("0000000000").email("dup@example.com").build());

    var request = new CreatePublisherRequest().name("Test").email("dup@example.com").phone("1111111111");

    var response = restTemplate.postForEntity(baseUri, request, String.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void should_get_all_publishers_ok() {
    publisherRepository.save(Publisher.builder().name("Publisher A").phone("1111111111").build());
    publisherRepository.save(Publisher.builder().name("Publisher B").phone("2222222222").build());

    var response = restTemplate.getForEntity(baseUri, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Publisher A"));
    assertTrue(response.getBody().contains("Publisher B"));
  }

  @Test
  void should_get_publisher_by_id_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Test").phone("1234567890").build());

    var response =
        restTemplate.getForEntity(baseUri + "/" + saved.getId(), PublisherResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(saved.getId(), response.getBody().getId());
    assertEquals("Test", response.getBody().getName());
  }

  @Test
  void should_fail_when_publisher_not_found() {
    var response = restTemplate.getForEntity(baseUri + "/" + UUID.randomUUID(), String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_update_publisher_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Original").phone("0000000000").build());

    var request = new CreatePublisherRequest().name("Updated").phone("9999999999");

    restTemplate.put(baseUri + "/" + saved.getId(), request);

    var response =
        restTemplate.getForEntity(baseUri + "/" + saved.getId(), PublisherResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Updated", response.getBody().getName());
  }

  @Test
  void should_fail_when_update_not_found() {
    var request = new CreatePublisherRequest().name("Updated").phone("9999999999");

    var response =
        restTemplate.exchange(
            baseUri + "/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new org.springframework.http.HttpEntity<>(request),
            String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void should_delete_publisher_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Test").phone("1234567890").build());

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
}
