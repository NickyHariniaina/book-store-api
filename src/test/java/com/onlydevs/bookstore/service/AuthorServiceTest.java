package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock private AuthorMapper authorMapper;
  @Mock private AuthorRepository authorRepository;
  @InjectMocks private AuthorService authorService;

  @Test
  void save_should_create_user() {
    var request = new CreateAuthorRequest().firstName("Albert").lastName("Camus");
    var authorToCreate = Author.builder().firstName("Albert").lastName("Camus").build();
    var savedAuthor =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Albert")
            .lastName("Camus")
            .createdAt(Instant.now())
            .build();
    var expectedResponse =
        new AuthorResponse()
            .id(savedAuthor.getId())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Albert Camus")
            .createdAt(savedAuthor.getCreatedAt());

    given(authorMapper.toDomain(request)).willReturn(authorToCreate);
    given(authorRepository.save(authorToCreate)).willReturn(savedAuthor);
    given(authorMapper.toRest(savedAuthor)).willReturn(expectedResponse);

    var actualResponse = authorService.save(request);

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
        new AuthorResponse().id(id).firstName("Albert").lastName("Camus").fullName("Albert Camus");
    given(authorRepository.findById(id)).willReturn(Optional.of(author));
    given(authorMapper.toRest(author)).willReturn(authorResponse);

    var actualResponse = authorService.getById(id.toString());

    assertEquals(authorResponse, actualResponse);
    then(authorMapper).should().toRest(author);
    then(authorRepository).should().findById(id);
  }

  @Test
  void should_raise_NotFoundException() {
    var id = UUID.randomUUID();

    given(authorRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.getById(id.toString()));
    then(authorRepository).should().findById(id);
  }

  @Test
  void getAll_should_return_list_of_authors() {
    var authors =
        List.of(
            Author.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Austen").build(),
            Author.builder().id(UUID.randomUUID()).firstName("Albert").lastName("Camus").build());
    var responses =
        List.of(
            new AuthorResponse()
                .id(authors.get(0).getId())
                .firstName("Jane")
                .lastName("Austen")
                .fullName("Jane Austen"),
            new AuthorResponse()
                .id(authors.get(1).getId())
                .firstName("Albert")
                .lastName("Camus")
                .fullName("Albert Camus"));

    given(authorRepository.findAll()).willReturn(authors);
    given(authorMapper.toRest(authors)).willReturn(responses);

    var result = authorService.getAll();

    assertEquals(2, result.size());
    then(authorRepository).should().findAll();
    then(authorMapper).should().toRest(authors);
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
    var request = new UpdateAuthorRequest().firstName("Emily").lastName("Bronte");
    var updatedAuthor =
        Author.builder()
            .id(id)
            .firstName("Emily")
            .lastName("Bronte")
            .createdAt(existingAuthor.getCreatedAt())
            .build();
    var response =
        new AuthorResponse()
            .id(id)
            .firstName("Emily")
            .lastName("Bronte")
            .fullName("Emily Bronte")
            .createdAt(existingAuthor.getCreatedAt());

    given(authorRepository.findById(id)).willReturn(Optional.of(existingAuthor));
    given(authorRepository.save(existingAuthor)).willReturn(updatedAuthor);
    given(authorMapper.toRest(updatedAuthor)).willReturn(response);

    var result = authorService.update(id.toString(), request);

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

    authorService.deleteById(id.toString());

    then(authorRepository).should().findById(id);
    then(authorRepository).should().delete(author);
  }

  @Test
  void deleteById_should_throw_when_not_found() {
    var id = UUID.randomUUID();

    given(authorRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.deleteById(id.toString()));
    then(authorRepository).should().findById(id);
  }
}
