package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
import java.time.Instant;
import java.util.List;
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
    var request = new CreateAuthorRequest().firstName("Jane").lastName("Austen");
    var response =
        new AuthorResponse()
            .id(UUID.randomUUID())
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen")
            .createdAt(Instant.now());
    given(authorService.save(request)).willReturn(response);

    AuthorResponse result = controller.createAuthor(request);

    assertSame(response, result);
    then(authorService).should().save(request);
  }

  @Test
  void getAll_should_delegate_to_service_and_return_list() {
    var responses =
        List.of(
            new AuthorResponse()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Austen")
                .fullName("Jane Austen"));
    given(authorService.getAll()).willReturn(responses);

    var result = controller.getAll();

    assertSame(responses, result);
    then(authorService).should().getAll();
  }

  @Test
  void getById_should_delegate_to_service_and_return_response() {
    var id = UUID.randomUUID().toString();
    var response =
        new AuthorResponse()
            .id(UUID.fromString(id))
            .firstName("Jane")
            .lastName("Austen")
            .fullName("Jane Austen");
    given(authorService.getById(id)).willReturn(response);

    AuthorResponse result = controller.getById(id);

    assertSame(response, result);
    then(authorService).should().getById(id);
  }

  @Test
  void update_should_delegate_to_service_and_return_response() {
    var id = UUID.randomUUID().toString();
    var request = new UpdateAuthorRequest().firstName("Emily").lastName("Bronte");
    var response =
        new AuthorResponse()
            .id(UUID.fromString(id))
            .firstName("Emily")
            .lastName("Bronte")
            .fullName("Emily Bronte");
    given(authorService.update(id, request)).willReturn(response);

    AuthorResponse result = controller.update(id, request);

    assertSame(response, result);
    then(authorService).should().update(id, request);
  }

  @Test
  void delete_should_delegate_to_service() {
    var id = UUID.randomUUID().toString();

    controller.delete(id);

    then(authorService).should().deleteById(id);
  }
}
