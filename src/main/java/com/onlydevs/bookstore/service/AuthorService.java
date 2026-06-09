package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.CreateAuthorResponse;
import com.onlydevs.bookstore.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
  public final AuthorRepository authorRepository;
  public final AuthorMapper authorMapper;

  public CreateAuthorResponse save(CreateAuthorRequest request) {
    var authorToCreate = authorMapper.toDomain(request);
    var authorCreated = authorRepository.save(authorToCreate);
    return authorMapper.toRest(authorCreated);
  }
}
