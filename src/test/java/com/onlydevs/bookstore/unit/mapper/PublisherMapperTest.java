package com.onlydevs.bookstore.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.PublisherResponse;
import com.onlydevs.bookstore.model.dto.UpdatePublisherRequest;
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
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    Publisher publisher =
        Publisher.builder()
            .id(id)
            .name("Test Publisher")
            .website("http://example.com")
            .email("test@example.com")
            .phone("1234567890")
            .country("US")
            .createdAt(now)
            .build();

    PublisherResponse response = mapper.toResponse(publisher);

    assertEquals(id, response.id);
    assertEquals("Test Publisher", response.name);
    assertEquals("http://example.com", response.website);
    assertEquals("test@example.com", response.email);
    assertEquals("1234567890", response.phone);
    assertEquals("US", response.country);
    assertEquals(now, response.createdAt);
  }

  @Test
  void toResponse_maps_nullable_fields() {
    Publisher publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Test Publisher")
            .createdAt(Instant.now())
            .build();

    PublisherResponse response = mapper.toResponse(publisher);

    assertNull(response.website);
    assertNull(response.email);
    assertNull(response.phone);
    assertNull(response.country);
  }

  @Test
  void toEntity_creates_publisher_from_request() {
    CreatePublisherRequest request =
        CreatePublisherRequest.builder()
            .name("Test Publisher")
            .website("http://example.com")
            .email("test@example.com")
            .phone("1234567890")
            .country("US")
            .build();

    Publisher publisher = mapper.toEntity(request);

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
    CreatePublisherRequest request = CreatePublisherRequest.builder().name("Minimal").build();

    Publisher publisher = mapper.toEntity(request);

    assertEquals("Minimal", publisher.getName());
    assertNull(publisher.getWebsite());
    assertNull(publisher.getEmail());
    assertNull(publisher.getPhone());
    assertNull(publisher.getCountry());
  }

  @Test
  void updateEntity_updates_all_fields() {
    Publisher publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Old Name")
            .website("http://old.com")
            .email("old@example.com")
            .phone("0000000000")
            .country("FR")
            .build();

    UpdatePublisherRequest request =
        UpdatePublisherRequest.builder()
            .name("New Name")
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE")
            .build();

    mapper.updateEntity(publisher, request);

    assertEquals("New Name", publisher.getName());
    assertEquals("http://new.com", publisher.getWebsite());
    assertEquals("new@example.com", publisher.getEmail());
    assertEquals("1111111111", publisher.getPhone());
    assertEquals("DE", publisher.getCountry());
  }

  @Test
  void updateEntity_does_not_overwrite_null_fields() {
    Publisher publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Original Name")
            .website("http://original.com")
            .email("original@example.com")
            .phone("1234567890")
            .country("US")
            .build();

    UpdatePublisherRequest request = UpdatePublisherRequest.builder().name("Updated Name").build();

    mapper.updateEntity(publisher, request);

    assertEquals("Updated Name", publisher.getName());
    assertEquals("http://original.com", publisher.getWebsite());
    assertEquals("original@example.com", publisher.getEmail());
    assertEquals("1234567890", publisher.getPhone());
    assertEquals("US", publisher.getCountry());
  }

  @Test
  void updateEntity_updates_all_nullable_fields_at_once() {
    Publisher publisher =
        Publisher.builder()
            .id(UUID.randomUUID())
            .name("Name")
            .website(null)
            .email(null)
            .phone(null)
            .country(null)
            .build();

    UpdatePublisherRequest request =
        UpdatePublisherRequest.builder()
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE")
            .build();

    mapper.updateEntity(publisher, request);

    assertEquals("http://new.com", publisher.getWebsite());
    assertEquals("new@example.com", publisher.getEmail());
    assertEquals("1111111111", publisher.getPhone());
    assertEquals("DE", publisher.getCountry());
  }
}
