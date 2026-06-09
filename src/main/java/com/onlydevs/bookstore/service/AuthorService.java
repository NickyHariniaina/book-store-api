package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
    public final AuthorRepository authorRepository;

    public CreateAuthorResponse save(CreateAuthorRequest request) {
        var authorToCreate = request.toAuthor();
        return authorRepository.save(authorToCreate);
    }
}