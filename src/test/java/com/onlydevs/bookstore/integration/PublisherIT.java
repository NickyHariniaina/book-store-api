package com.onlydevs.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.request.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.request.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

class PublisherIT extends FacadeIT {

  private WebTestClient webTestClient;

  @LocalServerPort int port;

  @Autowired private PublisherRepository publisherRepository;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    publisherRepository.deleteAll();
  }

  @Test
  void should_create_publisher_ok() {
    var request =
        CreatePublisherRequest.builder()
            .name("Test Publisher")
            .email("test@example.com")
            .phone("1234567890")
            .build();

    webTestClient
        .post()
        .uri("/api/v1/publishers")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(PublisherResponse.class)
        .value(
            response -> {
              assertNotNull(response.getId());
              assertEquals("Test Publisher", response.getName());
            });
  }

  @Test
  void should_fail_when_duplicate_email() {
    publisherRepository.save(
        Publisher.builder().name("Existing").phone("0000000000").email("dup@example.com").build());

    var request =
        CreatePublisherRequest.builder()
            .name("Test")
            .email("dup@example.com")
            .phone("1111111111")
            .build();

    webTestClient
        .post()
        .uri("/api/v1/publishers")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_get_all_publishers_ok() {
    publisherRepository.save(Publisher.builder().name("Publisher A").phone("1111111111").build());
    publisherRepository.save(Publisher.builder().name("Publisher B").phone("2222222222").build());

    webTestClient
        .get()
        .uri("/api/v1/publishers")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(
            body -> {
              assertNotNull(body);
              assertTrue(body.contains("Publisher A"));
              assertTrue(body.contains("Publisher B"));
            });
  }

  @Test
  void should_get_publisher_by_id_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Test").phone("1234567890").build());

    webTestClient
        .get()
        .uri("/api/v1/publishers/{id}", saved.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(PublisherResponse.class)
        .value(
            response -> {
              assertEquals(saved.getId(), response.getId());
              assertEquals("Test", response.getName());
            });
  }

  @Test
  void should_fail_when_publisher_not_found() {
    webTestClient
        .get()
        .uri("/api/v1/publishers/{id}", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_update_publisher_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Original").phone("0000000000").build());

    var request = UpdatePublisherRequest.builder().name("Updated").phone("9999999999").build();

    webTestClient
        .put()
        .uri("/api/v1/publishers/{id}", saved.getId())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk();

    webTestClient
        .get()
        .uri("/api/v1/publishers/{id}", saved.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(PublisherResponse.class)
        .value(response -> assertEquals("Updated", response.getName()));
  }

  @Test
  void should_fail_when_update_not_found() {
    var request = UpdatePublisherRequest.builder().name("Updated").phone("9999999999").build();

    webTestClient
        .put()
        .uri("/api/v1/publishers/{id}", UUID.randomUUID())
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_delete_publisher_ok() {
    var saved =
        publisherRepository.save(Publisher.builder().name("Test").phone("1234567890").build());

    webTestClient
        .delete()
        .uri("/api/v1/publishers/{id}", saved.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    assertFalse(publisherRepository.existsById(saved.getId()));
  }

  @Test
  void should_fail_when_delete_not_found() {
    webTestClient
        .delete()
        .uri("/api/v1/publishers/{id}", UUID.randomUUID())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void should_fail_when_invalid_uuid_for_get() {
    webTestClient
        .get()
        .uri("/api/v1/publishers/{id}", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_update() {
    var request = UpdatePublisherRequest.builder().name("Updated").build();

    webTestClient
        .put()
        .uri("/api/v1/publishers/{id}", "invalid-uuid")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void should_fail_when_invalid_uuid_for_delete() {
    webTestClient
        .delete()
        .uri("/api/v1/publishers/{id}", "invalid-uuid")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
