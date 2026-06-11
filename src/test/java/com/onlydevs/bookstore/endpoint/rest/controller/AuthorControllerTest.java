package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

  @Mock private AuthorService authorService;
  @InjectMocks private AuthorController controller;

  @Test
  void createAuthor_should_delegate_to_service_and_return_response() {
    var request = new CreateAuthorRequest("Jane", "Austen");
    var response =
        AuthorResponse.builder()
            .id(UUID.randomUUID().toString())
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen")
            .createdAt(Instant.now())
            .build();
    when(authorService.save(request)).thenReturn(response);

    AuthorResponse result = controller.createAuthor(request);

    assertSame(response, result);
    verify(authorService).save(request);
  }
}
