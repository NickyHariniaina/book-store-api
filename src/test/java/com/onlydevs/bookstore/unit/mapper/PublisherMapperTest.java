package com.onlydevs.bookstore.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.mapper.PublisherMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublisherMapperTest {

  private PublisherMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PublisherMapper();
  }

  @Test
  void toResponse_maps_all_fields() {
    var id = UUID.randomUUID();
    var now = Instant.now();
    var publisher =
        Publisher.builder()
            .id(id)
            .name("Test Publisher")
            .website("http://example.com")
            .email("test@example.com")
            .phone("1234567890")
            .country("US")
            .createdAt(now)
            .build();

    var response = mapper.toRest(publisher);

    assertEquals(id, response.getId());
    assertEquals("Test Publisher", response.getName());
    assertEquals("http://example.com", response.getWebsite());
    assertEquals("test@example.com", response.getEmail());
    assertEquals("1234567890", response.getPhone());
    assertEquals("US", response.getCountry());
    assertEquals(now, response.getCreatedAt());
  }

  @Test
  void toResponse_maps_nullable_fields() {
    var publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Test Publisher")
            .createdAt(Instant.now())
            .build();

    var response = mapper.toRest(publisher);

    assertNull(response.getWebsite());
    assertNull(response.getEmail());
    assertNull(response.getPhone());
    assertNull(response.getCountry());
  }

  @Test
  void toEntity_creates_publisher_from_request() {
    var request =
        new CreatePublisherRequest()
            .name("Test Publisher")
            .website("http://example.com")
            .email("test@example.com")
            .phone("1234567890")
            .country("US");

    var publisher = mapper.toDomain(request);

    assertNull(publisher.getId());
    assertEquals("Test Publisher", publisher.getName());
    assertEquals("http://example.com", publisher.getWebsite());
    assertEquals("test@example.com", publisher.getEmail());
    assertEquals("1234567890", publisher.getPhone());
    assertEquals("US", publisher.getCountry());
    assertNull(publisher.getCreatedAt());
  }

  @Test
  void toEntity_creates_publisher_with_minimal_fields() {
    var request = new CreatePublisherRequest().name("Minimal");

    var publisher = mapper.toDomain(request);

    assertEquals("Minimal", publisher.getName());
    assertNull(publisher.getWebsite());
    assertNull(publisher.getEmail());
    assertNull(publisher.getPhone());
    assertNull(publisher.getCountry());
  }

  @Test
  void updateEntity_updates_all_fields() {
    var publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Old Name")
            .website("http://old.com")
            .email("old@example.com")
            .phone("0000000000")
            .country("FR")
            .build();

    var request =
        new UpdatePublisherRequest()
            .name("New Name")
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE");

    mapper.updateEntity(publisher, request);

    assertEquals("New Name", publisher.getName());
    assertEquals("http://new.com", publisher.getWebsite());
    assertEquals("new@example.com", publisher.getEmail());
    assertEquals("1111111111", publisher.getPhone());
    assertEquals("DE", publisher.getCountry());
  }

  @Test
  void updateEntity_does_not_overwrite_null_fields() {
    var publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Original Name")
            .website("http://original.com")
            .email("original@example.com")
            .phone("1234567890")
            .country("US")
            .build();

    var request = new UpdatePublisherRequest().name("Updated Name");

    mapper.updateEntity(publisher, request);

    assertEquals("Updated Name", publisher.getName());
    assertEquals("http://original.com", publisher.getWebsite());
    assertEquals("original@example.com", publisher.getEmail());
    assertEquals("1234567890", publisher.getPhone());
    assertEquals("US", publisher.getCountry());
  }

  @Test
  void updateEntity_updates_all_nullable_fields_at_once() {
    var publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Name")
            .website(null)
            .email(null)
            .phone(null)
            .country(null)
            .build();

    var request =
        new UpdatePublisherRequest()
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE");

    mapper.updateEntity(publisher, request);

    assertEquals("http://new.com", publisher.getWebsite());
    assertEquals("new@example.com", publisher.getEmail());
    assertEquals("1111111111", publisher.getPhone());
    assertEquals("DE", publisher.getCountry());
  }
}
