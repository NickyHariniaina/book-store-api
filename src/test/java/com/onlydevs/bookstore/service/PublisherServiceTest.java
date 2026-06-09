package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.mapper.PublisherMapper;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

  @Mock private PublisherRepository publisherRepository;

  private PublisherMapper publisherMapper;

  private PublisherService publisherService;

  @BeforeEach
  void setUp() {
    publisherMapper = new PublisherMapper();
    publisherService = new PublisherService(publisherRepository, publisherMapper);
  }

  @Test
  void should_create_publisher_successfully() {
    var request =
        CreatePublisherRequest.builder()
            .name("Test Publisher")
            .email("test@example.com")
            .phone("1234567890")
            .build();
    var savedEntity = new Publisher();
    savedEntity.setId(UUID.randomUUID());
    savedEntity.setName("Test Publisher");
    savedEntity.setEmail("test@example.com");
    savedEntity.setPhone("1234567890");

    when(publisherRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
    when(publisherRepository.save(any())).thenReturn(savedEntity);

    var response = publisherService.createPublisher(request);

    assertEquals("Test Publisher", response.name);
    assertEquals("test@example.com", response.email);
    assertEquals("1234567890", response.phone);
    verify(publisherRepository).existsByEmailIgnoreCase("test@example.com");
    verify(publisherRepository).save(any());
  }

  @Test
  void should_throw_exception_when_create_publisher_with_duplicate_email() {
    var request =
        CreatePublisherRequest.builder()
            .name("Test Publisher")
            .email("duplicate@example.com")
            .build();

    when(publisherRepository.existsByEmailIgnoreCase("duplicate@example.com")).thenReturn(true);

    var ex = assertThrows(ConflictException.class, () -> publisherService.createPublisher(request));
    assertTrue(ex.getMessage().contains("duplicate@example.com"));
    verify(publisherRepository, never()).save(any());
  }

  @Test
  void should_update_publisher_name_successfully() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().name("New Name").build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setName("Old Name");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.save(existing)).thenReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("New Name", existing.getName());
    verify(publisherRepository).findById(id);
    verify(publisherRepository).save(existing);
  }

  @Test
  void should_update_publisher_email_successfully() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().email("new@example.com").build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setEmail("old@example.com");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
    when(publisherRepository.save(existing)).thenReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("new@example.com", existing.getEmail());
    verify(publisherRepository).existsByEmailIgnoreCase("new@example.com");
  }

  @Test
  void should_update_publisher_country_successfully() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().country("DE").build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setCountry("US");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.save(existing)).thenReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("DE", existing.getCountry());
  }

  @Test
  void should_update_publisher_phone_successfully() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().phone("1111111111").build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setPhone("0000000000");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.save(existing)).thenReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("1111111111", existing.getPhone());
  }

  @Test
  void should_throw_exception_when_update_publisher_with_duplicate_email() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().email("taken@example.com").build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setEmail("old@example.com");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

    var ex =
        assertThrows(ConflictException.class, () -> publisherService.updatePublisher(id, request));
    assertTrue(ex.getMessage().contains("taken@example.com"));
    verify(publisherRepository, never()).save(any());
  }

  @Test
  void should_throw_exception_when_update_nonexistent_publisher() {
    var id = UUID.randomUUID();
    var request = UpdatePublisherRequest.builder().name("New Name").build();

    when(publisherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.updatePublisher(id, request));
    verify(publisherRepository, never()).save(any());
  }

  @Test
  void should_delete_publisher_when_no_editions_linked() {
    var id = UUID.randomUUID();
    var publisher = new Publisher();
    publisher.setId(id);

    when(publisherRepository.findById(id)).thenReturn(Optional.of(publisher));

    publisherService.deletePublisher(id);

    verify(publisherRepository).delete(publisher);
  }

  @Test
  void should_throw_exception_when_delete_nonexistent_publisher() {
    var id = UUID.randomUUID();

    when(publisherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(id));
    verify(publisherRepository, never()).delete(any());
  }

  @Test
  void should_get_all_publishers_with_pagination() {
    var pageable = PageRequest.of(0, 10);

    var publisher = new Publisher();
    publisher.setId(UUID.randomUUID());
    publisher.setName("Test");

    var publisherPage = new PageImpl<>(java.util.List.of(publisher));

    when(publisherRepository.findAll(pageable)).thenReturn(publisherPage);

    var result = publisherService.getAllPublishers(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("Test", result.getContent().get(0).name);
    verify(publisherRepository).findAll(pageable);
  }

  @Test
  void should_return_empty_page_when_no_publishers_exist() {
    var pageable = PageRequest.of(0, 10);
    Page<Publisher> emptyPage = Page.empty();

    when(publisherRepository.findAll(pageable)).thenReturn(emptyPage);

    var result = publisherService.getAllPublishers(pageable);

    assertTrue(result.isEmpty());
  }

  @Test
  void should_get_publisher_by_id_when_exists() {
    var id = UUID.randomUUID();
    var publisher = new Publisher();
    publisher.setId(id);
    publisher.setName("Test Publisher");

    when(publisherRepository.findById(id)).thenReturn(Optional.of(publisher));

    var response = publisherService.getPublisherById(id);

    assertEquals("Test Publisher", response.name);
    assertEquals(id, response.id);
  }

  @Test
  void should_throw_exception_when_get_publisher_by_id_not_found() {
    var id = UUID.randomUUID();

    when(publisherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.getPublisherById(id));
  }

  @Test
  void should_update_all_nullable_contact_fields_at_once() {
    var id = UUID.randomUUID();
    var request =
        UpdatePublisherRequest.builder()
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE")
            .build();
    var existing = new Publisher();
    existing.setId(id);
    existing.setWebsite(null);
    existing.setEmail(null);
    existing.setPhone(null);
    existing.setCountry(null);

    when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(publisherRepository.save(existing)).thenReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("http://new.com", existing.getWebsite());
    assertEquals("new@example.com", existing.getEmail());
    assertEquals("1111111111", existing.getPhone());
    assertEquals("DE", existing.getCountry());
  }
}
