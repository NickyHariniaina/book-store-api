package com.onlydevs.bookstore.endpoint.rest;

import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.exception.NotImplementedException;
import com.onlydevs.bookstore.model.exception.TooManyRequestsException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {BadRequestException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleBadRequest(
      BadRequestException e) {
    log.info("Bad request", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MissingServletRequestParameterException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleBadRequest(
      MissingServletRequestParameterException e) {
    log.info("Missing parameter", e);
    return handleBadRequest(new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleConversionFailed(
      MethodArgumentTypeMismatchException e) {
    log.info("Conversion failed", e);
    String message = e.getCause().getCause().getMessage();
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleValidation(
      MethodArgumentNotValidException e) {
    log.info("Validation failed", e);
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse(e.getMessage());
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {TooManyRequestsException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleTooManyRequests(
      TooManyRequestsException e) {
    log.info("Too many requests", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.TOO_MANY_REQUESTS), HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(
      value = {
        LockAcquisitionException.class,
        CannotAcquireLockException.class,
        OptimisticLockException.class
      })
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception>
      handleLockAcquisitionException(Exception e) {
    log.warn("Database lock could not be acquired: too many requests assumed", e);
    return handleTooManyRequests(new TooManyRequestsException(e));
  }

  @ExceptionHandler(value = {ConflictException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleConflict(
      ConflictException e) {
    log.info("Conflict", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleNotFound(
      NotFoundException e) {
    log.info("Not found", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {NotImplementedException.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleNotImplemented(
      NotImplementedException e) {
    log.error("Not implemented", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(value = {Exception.class})
  ResponseEntity<com.onlydevs.bookstore.endpoint.rest.model.Exception> handleDefault(Exception e) {
    log.error("Internal error", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private com.onlydevs.bookstore.endpoint.rest.model.Exception toRest(
      Exception e, HttpStatus status) {
    var restException = new com.onlydevs.bookstore.endpoint.rest.model.Exception();
    restException.setType(status.toString());
    restException.setMessage(e.getMessage());
    return restException;
  }
}
