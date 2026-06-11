package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.time.Instant;
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
    var request = new CreateAuthorRequest("Albert", "Camus");
    var authorToCreate =
        Author.builder().firstName(request.firstName()).lastName(request.lastName()).build();
    var savedAuthor =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Albert")
            .lastName("Camus")
            .createdAt(Instant.now())
            .build();
    var expectedResponse =
        AuthorResponse.builder()
            .id(savedAuthor.getId().toString())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Alber Camus")
            .createdAt(savedAuthor.getCreatedAt())
            .build();

    when(authorMapper.toDomain(request)).thenReturn(authorToCreate);
    when(authorRepository.save(authorToCreate)).thenReturn(savedAuthor);
    when(authorMapper.toRest(savedAuthor)).thenReturn(expectedResponse);

    AuthorResponse actualResponse = authorService.save(request);

    assertEquals(expectedResponse, actualResponse);
    verify(authorMapper).toDomain(request);
    verify(authorRepository).save(authorToCreate);
    verify(authorMapper).toRest(savedAuthor);
  }

  @Test
  void should_find_author_by_id() {
    var id = UUID.randomUUID();
    var author = Author.builder().id(id).firstName("Albert").lastName("Camus").build();
    var authorResponse =
        AuthorResponse.builder()
            .id(id.toString())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Alber Camus")
            .build();
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));
    when(authorMapper.toRest(author)).thenReturn(authorResponse);

    AuthorResponse actualResponse = authorService.getById(id.toString());

    assertEquals(authorResponse, actualResponse);
    verify(authorMapper).toRest(author);
    verify(authorRepository).findById(id);
  }

  @Test
  void should_raise_NotFoundException() {
    var id = UUID.randomUUID();

    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> {
          authorService.getById(id.toString());
        });
    verify(authorRepository).findById(id);
  }
}
