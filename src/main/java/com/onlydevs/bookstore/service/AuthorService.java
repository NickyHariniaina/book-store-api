package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.AuthorMapper;
import com.onlydevs.bookstore.model.dto.request.CreateAuthorRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateAuthorRequest;
import com.onlydevs.bookstore.model.dto.response.AuthorResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final AuthorMapper authorMapper;

  public AuthorResponse createAuthor(CreateAuthorRequest request) {
    var authorToCreate = authorMapper.toDomain(request);
    var authorCreated = authorRepository.save(authorToCreate);
    return authorMapper.toRest(authorCreated);
  }

  public AuthorResponse getById(UUID id) {
    var author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    return authorMapper.toRest(author);
  }

  public Page<AuthorResponse> getAllAuthors(Pageable pageable) {
    return authorRepository.findAll(pageable).map(authorMapper::toRest);
  }

  public AuthorResponse update(UUID id, UpdateAuthorRequest request) {
    var author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    if (request.getFirstName() != null) {
      author.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      author.setLastName(request.getLastName());
    }
    return authorMapper.toRest(authorRepository.save(author));
  }

  public void deleteById(UUID id) {
    var author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " doesn't exist."));
    authorRepository.delete(author);
  }
}
