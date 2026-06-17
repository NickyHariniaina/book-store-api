package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
  public final AuthorRepository authorRepository;
  public final AuthorMapper authorMapper;

  public AuthorResponse save(CreateAuthorRequest request) {
    var authorToCreate = authorMapper.toDomain(request);
    var authorCreated = authorRepository.save(authorToCreate);
    return authorMapper.toRest(authorCreated);
  }

  public AuthorResponse getById(String id) {
    var author =
        authorRepository
            .findById(UUID.fromString(id))
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    return authorMapper.toRest(author);
  }

  public List<AuthorResponse> getAll() {
    return authorMapper.toRest(authorRepository.findAll());
  }

  public AuthorResponse update(String id, UpdateAuthorRequest request) {
    var author =
        authorRepository
            .findById(UUID.fromString(id))
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    if (request.getFirstName() != null) {
      author.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      author.setLastName(request.getLastName());
    }
    return authorMapper.toRest(authorRepository.save(author));
  }

  public void deleteById(String id) {
    var author =
        authorRepository
            .findById(UUID.fromString(id))
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    authorRepository.delete(author);
  }
}
