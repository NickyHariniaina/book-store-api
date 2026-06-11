package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
  public Author toDomain(CreateAuthorRequest request) {
    return Author.builder().firstName(request.firstName()).lastName(request.lastName()).build();
  }

  public AuthorResponse toRest(Author author) {
    return AuthorResponse.builder()
        .id(author.getId().toString())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .fullName(author.getFullName())
        .createdAt(author.getCreatedAt())
        .build();
  }
}
