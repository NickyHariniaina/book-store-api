package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.AuthorResponse;
import com.onlydevs.bookstore.model.dto.CreateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  @PostMapping
  public AuthorResponse createAuthor(@RequestBody @Valid CreateAuthorRequest request) {
    return authorService.save(request);
  }

  @GetMapping("/{id}")
  public AuthorResponse getById(@PathVariable String id) {
    return authorService.getById(id);
  }
}
