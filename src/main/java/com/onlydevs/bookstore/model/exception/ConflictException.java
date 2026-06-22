package com.onlydevs.bookstore.model.exception;

public class ConflictException extends ApiException {
  public ConflictException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
