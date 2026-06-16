package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.model.AuthorResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateAuthorRequest;
import com.onlydevs.bookstore.endpoint.rest.model.UpdateAuthorRequest;
import com.onlydevs.bookstore.service.AuthorService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  @PostMapping
  public AuthorResponse createAuthor(@RequestBody CreateAuthorRequest request) {
    return authorService.save(request);
  }

  @GetMapping
  public List<AuthorResponse> getAll() {
    return authorService.getAll();
  }

  @GetMapping("/{id}")
  public AuthorResponse getById(@PathVariable String id) {
    return authorService.getById(id);
  }

  @PutMapping("/{id}")
  public AuthorResponse update(@PathVariable String id, @RequestBody UpdateAuthorRequest request) {
    return authorService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    authorService.deleteById(id);
  }
}
