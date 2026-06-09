package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookStoreService {

  private final BookStoreRepository repository;

  public List<BookStore> findAll() {
    return repository.findAll();
  }

  public BookStore findById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("BookStore not found: " + id));
  }

  public BookStore save(BookStore bookStore) {
    return repository.save(bookStore);
  }

  public BookStore update(UUID id, String name, String address, String phone, String email) {
    BookStore existing = findById(id);
    if (name != null) {
      existing.setName(name);
    }
    if (address != null) {
      existing.setAddress(address);
    }
    if (phone != null) {
      existing.setPhone(phone);
    }
    if (email != null) {
      existing.setEmail(email);
    }
    return repository.save(existing);
  }
}