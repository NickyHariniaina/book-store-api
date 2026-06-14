package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

  @Mock private PublisherRepository publisherRepository;

  @Mock private PublisherMapper publisherMapper;

  @InjectMocks private PublisherService publisherService;

  private Publisher publisher;
  private PublisherResponse publisherResponse;
  private UUID publisherId;

  @BeforeEach
  void setUp() {
    publisherId = UUID.randomUUID();
    publisher = Publisher.builder().id(publisherId).name("Test Publisher").build();
    publisherResponse = new PublisherResponse().id(publisherId).name("Test Publisher");
  }

  @Test
  void create_publisher_ok_when_email_not_taken() {
    var request = new CreatePublisherRequest().name("Test Publisher").email("test@example.com");
    given(publisherRepository.existsByEmailIgnoreCase("test@example.com")).willReturn(false);
    given(publisherMapper.toDomain(request)).willReturn(publisher);
    given(publisherRepository.save(publisher)).willReturn(publisher);
    given(publisherMapper.toRest(publisher)).willReturn(publisherResponse);

    var actual = publisherService.createPublisher(request);

    assertEquals(publisherResponse.getName(), actual.getName());
    then(publisherRepository).should().existsByEmailIgnoreCase("test@example.com");
    then(publisherMapper).should().toDomain(request);
    then(publisherRepository).should().save(publisher);
    then(publisherMapper).should().toRest(publisher);
  }

  @Test
  void create_publisher_ko_when_email_already_taken() {
    var request = new CreatePublisherRequest().name("Test Publisher").email("taken@example.com");
    given(publisherRepository.existsByEmailIgnoreCase("taken@example.com")).willReturn(true);

    assertThrows(ConflictException.class, () -> publisherService.createPublisher(request));
    then(publisherRepository).should().existsByEmailIgnoreCase("taken@example.com");
    then(publisherMapper).shouldHaveNoInteractions();
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void update_publisher_ok_when_email_not_taken() {
    var request = new UpdatePublisherRequest().name("Updated Name");
    var updatedPublisher = Publisher.builder().id(publisherId).name("Updated Name").build();
    var updatedResponse = new PublisherResponse().id(publisherId).name("Updated Name");

    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(publisherRepository.save(publisher)).willReturn(updatedPublisher);
    given(publisherMapper.toRest(updatedPublisher)).willReturn(updatedResponse);

    var actual = publisherService.updatePublisher(publisherId, request);

    assertEquals(updatedResponse.getName(), actual.getName());
    then(publisherRepository).should().findById(publisherId);
    then(publisherRepository).should().save(publisher);
    then(publisherMapper).should().toRest(updatedPublisher);
  }

  @Test
  void update_publisher_ok_when_email_changed_and_not_taken() {
    var request = new UpdatePublisherRequest().email("new@example.com");
    publisher.setEmail("old@example.com");
    var updatedPublisher =
        Publisher.builder().id(publisherId).name("Test Publisher").email("new@example.com").build();
    var updatedResponse =
        new PublisherResponse().id(publisherId).name("Test Publisher").email("new@example.com");

    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(publisherRepository.existsByEmailIgnoreCase("new@example.com")).willReturn(false);
    given(publisherRepository.save(publisher)).willReturn(updatedPublisher);
    given(publisherMapper.toRest(updatedPublisher)).willReturn(updatedResponse);

    publisherService.updatePublisher(publisherId, request);

    then(publisherRepository).should().existsByEmailIgnoreCase("new@example.com");
  }

  @Test
  void update_publisher_ko_when_email_already_taken() {
    var request = new UpdatePublisherRequest().email("taken@example.com");
    publisher.setEmail("old@example.com");

    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(publisherRepository.existsByEmailIgnoreCase("taken@example.com")).willReturn(true);

    assertThrows(
        ConflictException.class, () -> publisherService.updatePublisher(publisherId, request));
    then(publisherRepository).should().findById(publisherId);
    then(publisherRepository).should().existsByEmailIgnoreCase("taken@example.com");
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void update_publisher_ko_when_publisher_not_found() {
    var request = new UpdatePublisherRequest().name("New Name");

    given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> publisherService.updatePublisher(publisherId, request));
    then(publisherRepository).should().findById(publisherId);
    then(publisherRepository).should(never()).save(any());
  }

  @Test
  void delete_publisher_ok_when_exists() {
    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));

    publisherService.deletePublisher(publisherId);

    then(publisherRepository).should().findById(publisherId);
    then(publisherRepository).should().delete(publisher);
  }

  @Test
  void delete_publisher_ko_when_not_found() {
    given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(publisherId));
    then(publisherRepository).should().findById(publisherId);
    then(publisherRepository).should(never()).delete(any());
  }

  @Test
  void get_all_publishers_ok_when_publishers_exist() {
    var pageable = PageRequest.of(0, 10);
    var page = new PageImpl<>(java.util.List.of(publisher));

    given(publisherRepository.findAll(pageable)).willReturn(page);
    given(publisherMapper.toRest(publisher)).willReturn(publisherResponse);

    var result = publisherService.getAllPublishers(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(publisherResponse.getName(), result.getContent().get(0).getName());
    then(publisherRepository).should().findAll(pageable);
    then(publisherMapper).should().toRest(publisher);
  }

  @Test
  void get_all_publishers_ok_when_no_publishers() {
    var pageable = PageRequest.of(0, 10);

    given(publisherRepository.findAll(pageable)).willReturn(Page.empty());

    var result = publisherService.getAllPublishers(pageable);

    assertTrue(result.isEmpty());
    then(publisherRepository).should().findAll(pageable);
  }

  @Test
  void get_publisher_by_id_ok_when_exists() {
    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(publisherMapper.toRest(publisher)).willReturn(publisherResponse);

    var actual = publisherService.getPublisherById(publisherId);

    assertEquals(publisherResponse.getName(), actual.getName());
    then(publisherRepository).should().findById(publisherId);
    then(publisherMapper).should().toRest(publisher);
  }

  @Test
  void get_publisher_by_id_ko_when_not_found() {
    given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> publisherService.getPublisherById(publisherId));
    then(publisherRepository).should().findById(publisherId);
  }
}
