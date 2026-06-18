package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
  public Author toDomain(CreateAuthorRequest request) {
    return Author.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .build();
  }

  public AuthorResponse toRest(Author author) {
    return AuthorResponse.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .fullName(author.getFullName())
        .createdAt(author.getCreatedAt())
        .build();
  }

  public List<AuthorResponse> toRest(List<Author> authors) {
    return authors.stream().map(this::toRest).toList();
  }
}
