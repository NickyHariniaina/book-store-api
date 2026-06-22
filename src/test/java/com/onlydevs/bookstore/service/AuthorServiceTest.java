package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.time.Instant;
import java.util.List;
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
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock private AuthorMapper authorMapper;
  @Mock private AuthorRepository authorRepository;
  @InjectMocks private AuthorService authorService;

  private Author janeAusten;
  private Author albertCamus;
  private AuthorResponse janeResponse;
  private AuthorResponse albertResponse;

  @BeforeEach
  void setUp() {
    janeAusten =
        Author.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Austen").build();
    albertCamus =
        Author.builder().id(UUID.randomUUID()).firstName("Albert").lastName("Camus").build();
    janeResponse =
        AuthorResponse.builder()
            .id(janeAusten.getId())
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen")
            .build();
    albertResponse =
        AuthorResponse.builder()
            .id(albertCamus.getId())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Albert Camus")
            .build();
  }

  @Test
  void createAuthor_should_persist_and_return() {
    var request = CreateAuthorRequest.builder().firstName("Albert").lastName("Camus").build();
    var authorToCreate = Author.builder().firstName("Albert").lastName("Camus").build();
    var savedAuthor =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Albert")
            .lastName("Camus")
            .createdAt(Instant.now())
            .build();
    var expectedResponse =
        AuthorResponse.builder()
            .id(savedAuthor.getId())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Albert Camus")
            .createdAt(savedAuthor.getCreatedAt())
            .build();

    given(authorMapper.toDomain(request)).willReturn(authorToCreate);
    given(authorRepository.save(authorToCreate)).willReturn(savedAuthor);
    given(authorMapper.toRest(savedAuthor)).willReturn(expectedResponse);

    var actualResponse = authorService.createAuthor(request);

    assertEquals(expectedResponse, actualResponse);
    then(authorMapper).should().toDomain(request);
    then(authorRepository).should().save(authorToCreate);
    then(authorMapper).should().toRest(savedAuthor);
  }

  @Test
  void should_find_author_by_id() {
    var id = UUID.randomUUID();
    var author = Author.builder().id(id).firstName("Albert").lastName("Camus").build();
    var authorResponse =
        AuthorResponse.builder()
            .id(id)
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Albert Camus")
            .build();
    given(authorRepository.findById(id)).willReturn(Optional.of(author));
    given(authorMapper.toRest(author)).willReturn(authorResponse);

    var actualResponse = authorService.getById(id);

    assertEquals(authorResponse, actualResponse);
    then(authorMapper).should().toRest(author);
    then(authorRepository).should().findById(id);
  }

  @Test
  void should_raise_NotFoundException() {
    var id = UUID.randomUUID();

    given(authorRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.getById(id));
    then(authorRepository).should().findById(id);
  }

  @Test
  void getAllAuthors_should_return_page() {
    Pageable pageable = PageRequest.of(0, 20);
    var authors = List.of(janeAusten, albertCamus);
    Page<Author> authorPage = new PageImpl<>(authors, pageable, 2);

    given(authorRepository.findAll(pageable)).willReturn(authorPage);
    given(authorMapper.toRest(janeAusten)).willReturn(janeResponse);
    given(authorMapper.toRest(albertCamus)).willReturn(albertResponse);

    var result = authorService.getAllAuthors(pageable);

    assertEquals(2, result.getContent().size());
    assertEquals("Jane Austen", result.getContent().get(0).getFullName());
    then(authorRepository).should().findAll(pageable);
    then(authorMapper).should().toRest(janeAusten);
    then(authorMapper).should().toRest(albertCamus);
  }

  @Test
  void getAllAuthors_should_return_empty_page() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<Author> emptyPage = Page.empty(pageable);

    given(authorRepository.findAll(pageable)).willReturn(emptyPage);

    var result = authorService.getAllAuthors(pageable);

    assertTrue(result.isEmpty());
    then(authorRepository).should().findAll(pageable);
  }

  @Test
  void update_should_modify_and_return_author() {
    var id = UUID.randomUUID();
    var existingAuthor =
        Author.builder()
            .id(id)
            .firstName("Jane")
            .lastName("Austen")
            .createdAt(Instant.now())
            .build();
    var request = UpdateAuthorRequest.builder().firstName("Emily").lastName("Bronte").build();
    var updatedAuthor =
        Author.builder()
            .id(id)
            .firstName("Emily")
            .lastName("Bronte")
            .createdAt(existingAuthor.getCreatedAt())
            .build();
    var response =
        AuthorResponse.builder()
            .id(id)
            .firstName("Emily")
            .lastName("Bronte")
            .fullName("Emily Bronte")
            .createdAt(existingAuthor.getCreatedAt())
            .build();

    given(authorRepository.findById(id)).willReturn(Optional.of(existingAuthor));
    given(authorRepository.save(existingAuthor)).willReturn(updatedAuthor);
    given(authorMapper.toRest(updatedAuthor)).willReturn(response);

    var result = authorService.update(id, request);

    assertEquals(response, result);
    then(authorRepository).should().findById(id);
    then(authorRepository).should().save(existingAuthor);
    then(authorMapper).should().toRest(updatedAuthor);
  }

  @Test
  void deleteById_should_remove_author() {
    var id = UUID.randomUUID();
    var author = Author.builder().id(id).firstName("Jane").lastName("Austen").build();

    given(authorRepository.findById(id)).willReturn(Optional.of(author));

    authorService.deleteById(id);

    then(authorRepository).should().findById(id);
    then(authorRepository).should().delete(author);
  }

  @Test
  void deleteById_should_throw_when_not_found() {
    var id = UUID.randomUUID();

    given(authorRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.deleteById(id));
    then(authorRepository).should().findById(id);
  }
}
