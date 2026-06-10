package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.CreateAuthorResponse;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.time.Instant;
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
  void save_should_create_user_ok() {
    var request = new CreateAuthorRequest("Albert", "Camus");
    var authorToCreate =
        Author.builder().firstName(request.firstName()).lastName(request.lastName()).build();
    var savedAuthor =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Alber")
            .lastName("Camus")
            .createdAt(Instant.now())
            .build();
    var expectedResponse =
        CreateAuthorResponse.builder()
            .id(savedAuthor.getId().toString())
            .firstName("Albert")
            .lastName("Camus")
            .fullName("Alber Camus")
            .createdAt(savedAuthor.getCreatedAt())
            .build();

    when(authorMapper.toDomain(request)).thenReturn(authorToCreate);
    when(authorRepository.save(authorToCreate)).thenReturn(savedAuthor);
    when(authorMapper.toRest(savedAuthor)).thenReturn(expectedResponse);

    CreateAuthorResponse actualResponse = authorService.save(request);

    assertEquals(expectedResponse, actualResponse);
    verify(authorMapper).toDomain(request);
    verify(authorRepository).save(authorToCreate);
    verify(authorMapper).toRest(savedAuthor);
  }
}
