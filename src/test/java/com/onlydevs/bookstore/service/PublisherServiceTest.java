package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.Publisher;
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
        new CreatePublisherRequest()
            .name("Test Publisher")
            .email("test@example.com")
            .phone("1234567890");
    var savedEntity = new Publisher();
    savedEntity.setId(UUID.randomUUID());
    savedEntity.setName("Test Publisher");
    savedEntity.setEmail("test@example.com");
    savedEntity.setPhone("1234567890");

    given(publisherRepository.existsByEmailIgnoreCase("test@example.com")).willReturn(false);
    given(publisherRepository.save(any())).willReturn(savedEntity);

    var response = publisherService.createPublisher(request);

    assertEquals("Test Publisher", response.getName());
    assertEquals("test@example.com", response.getEmail());
    assertEquals("1234567890", response.getPhone());
    then(publisherRepository).should().existsByEmailIgnoreCase("test@example.com");
    then(publisherRepository).should().save(any());
  }

  @Test
  void should_throw_exception_when_create_publisher_with_duplicate_email() {
    var request =
        new CreatePublisherRequest()
            .name("Test Publisher")
            .email("duplicate@example.com");

    given(publisherRepository.existsByEmailIgnoreCase("duplicate@example.com")).willReturn(true);

    var ex = assertThrows(ConflictException.class, () -> publisherService.createPublisher(request));
    assertTrue(ex.getMessage().contains("duplicate@example.com"));
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void should_update_publisher_name_successfully() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().name("New Name");
    var existing = new Publisher();
    existing.setId(id);
    existing.setName("Old Name");

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.save(existing)).willReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("New Name", existing.getName());
    then(publisherRepository).should().findById(id);
    then(publisherRepository).should().save(existing);
  }

  @Test
  void should_update_publisher_email_successfully() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().email("new@example.com");
    var existing = new Publisher();
    existing.setId(id);
    existing.setEmail("old@example.com");

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.existsByEmailIgnoreCase("new@example.com")).willReturn(false);
    given(publisherRepository.save(existing)).willReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("new@example.com", existing.getEmail());
    then(publisherRepository).should().existsByEmailIgnoreCase("new@example.com");
  }

  @Test
  void should_update_publisher_country_successfully() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().country("DE");
    var existing = new Publisher();
    existing.setId(id);
    existing.setCountry("US");

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.save(existing)).willReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("DE", existing.getCountry());
  }

  @Test
  void should_update_publisher_phone_successfully() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().phone("1111111111");
    var existing = new Publisher();
    existing.setId(id);
    existing.setPhone("0000000000");

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.save(existing)).willReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("1111111111", existing.getPhone());
  }

  @Test
  void should_throw_exception_when_update_publisher_with_duplicate_email() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().email("taken@example.com");
    var existing = new Publisher();
    existing.setId(id);
    existing.setEmail("old@example.com");

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.existsByEmailIgnoreCase("taken@example.com")).willReturn(true);

    var ex =
        assertThrows(ConflictException.class, () -> publisherService.updatePublisher(id, request));
    assertTrue(ex.getMessage().contains("taken@example.com"));
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void should_throw_exception_when_update_nonexistent_publisher() {
    var id = UUID.randomUUID();
    var request = new UpdatePublisherRequest().name("New Name");

    given(publisherRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.updatePublisher(id, request));
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void should_delete_publisher_when_no_editions_linked() {
    var id = UUID.randomUUID();
    var publisher = new Publisher();
    publisher.setId(id);

    given(publisherRepository.findById(id)).willReturn(Optional.of(publisher));

    publisherService.deletePublisher(id);

    then(publisherRepository).should().delete(publisher);
  }

  @Test
  void should_throw_exception_when_delete_nonexistent_publisher() {
    var id = UUID.randomUUID();

    given(publisherRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(id));
    then(publisherRepository).should(never()).delete(any());
  }

  @Test
  void should_get_all_publishers_with_pagination() {
    var pageable = PageRequest.of(0, 10);

    var publisher = new Publisher();
    publisher.setId(UUID.randomUUID());
    publisher.setName("Test");

    var publisherPage = new PageImpl<>(java.util.List.of(publisher));

    given(publisherRepository.findAll(pageable)).willReturn(publisherPage);

    var result = publisherService.getAllPublishers(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("Test", result.getContent().get(0).getName());
    then(publisherRepository).should().findAll(pageable);
  }

  @Test
  void should_return_empty_page_when_no_publishers_exist() {
    var pageable = PageRequest.of(0, 10);
    Page<Publisher> emptyPage = Page.empty();

    given(publisherRepository.findAll(pageable)).willReturn(emptyPage);

    var result = publisherService.getAllPublishers(pageable);

    assertTrue(result.isEmpty());
  }

  @Test
  void should_get_publisher_by_id_when_exists() {
    var id = UUID.randomUUID();
    var publisher = new Publisher();
    publisher.setId(id);
    publisher.setName("Test Publisher");

    given(publisherRepository.findById(id)).willReturn(Optional.of(publisher));

    var response = publisherService.getPublisherById(id);

    assertEquals("Test Publisher", response.getName());
    assertEquals(id, response.getId());
  }

  @Test
  void should_throw_exception_when_get_publisher_by_id_not_found() {
    var id = UUID.randomUUID();

    given(publisherRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.getPublisherById(id));
  }

  @Test
  void should_update_all_nullable_contact_fields_at_once() {
    var id = UUID.randomUUID();
    var request =
        new UpdatePublisherRequest()
            .website("http://new.com")
            .email("new@example.com")
            .phone("1111111111")
            .country("DE");
    var existing = new Publisher();
    existing.setId(id);
    existing.setWebsite(null);
    existing.setEmail(null);
    existing.setPhone(null);
    existing.setCountry(null);

    given(publisherRepository.findById(id)).willReturn(Optional.of(existing));
    given(publisherRepository.save(existing)).willReturn(existing);

    publisherService.updatePublisher(id, request);

    assertEquals("http://new.com", existing.getWebsite());
    assertEquals("new@example.com", existing.getEmail());
    assertEquals("1111111111", existing.getPhone());
    assertEquals("DE", existing.getCountry());
  }
}
