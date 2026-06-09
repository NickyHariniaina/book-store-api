package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.CreateAuthorResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthorMapper {
  public Author toDomain(CreateAuthorRequest request) {
    return Author.builder().firstName(request.firstName()).lastName(request.lastName()).build();
  }

  public CreateAuthorResponse toRest(Author author) {
    return CreateAuthorResponse.builder()
        .id(author.getId().toString())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .fullName(author.getFullName())
        .createdAt(author.getCreatedAt())
        .build();
  }
}
