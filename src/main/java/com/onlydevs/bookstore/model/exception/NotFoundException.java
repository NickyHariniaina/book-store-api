package com.onlydevs.bookstore.model.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String entityName, UUID id) {
    super(entityName + " not found with id: " + id);
  }
}
