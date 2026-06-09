package com.onlydevs.bookstore.model.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
  public NotFoundException(String entityName, UUID id) {
    super(entityName + " not found with id: " + id);
  }
}
