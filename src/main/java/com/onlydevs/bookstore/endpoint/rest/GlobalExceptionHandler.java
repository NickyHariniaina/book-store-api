package com.onlydevs.bookstore.endpoint.rest;

import com.onlydevs.bookstore.endpoint.rest.model.RestErrorResponse;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.exception.NotImplementedException;
import com.onlydevs.bookstore.model.exception.TooManyRequestsException;
import jakarta.persistence.OptimisticLockException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {BadRequestException.class})
  ResponseEntity<RestErrorResponse> handleBadRequest(BadRequestException e) {
    log.info("Bad request", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MissingServletRequestParameterException.class})
  ResponseEntity<RestErrorResponse> handleBadRequest(MissingServletRequestParameterException e) {
    log.info("Missing parameter", e);
    return handleBadRequest(new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  ResponseEntity<RestErrorResponse> handleConversionFailed(MethodArgumentTypeMismatchException e) {
    log.info("Conversion failed for parameter '{}' with value '{}'", e.getName(), e.getValue());
    String message;
    if (e.getRequiredType() == UUID.class) {
      message =
          String.format(
              "Invalid UUID format: '%s'. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
              e.getValue());
    } else if (e.getCause() != null) {
      message = e.getCause().getMessage();
    } else {
      message = String.format("Invalid value '%s' for parameter '%s'", e.getValue(), e.getName());
    }
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  ResponseEntity<RestErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    log.info("Validation failed", e);
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse(e.getMessage());
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {TooManyRequestsException.class})
  ResponseEntity<RestErrorResponse> handleTooManyRequests(TooManyRequestsException e) {
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
  ResponseEntity<RestErrorResponse> handleLockAcquisitionException(Exception e) {
    log.warn("Database lock could not be acquired: too many requests assumed", e);
    return handleTooManyRequests(new TooManyRequestsException(e));
  }

  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<RestErrorResponse> handleNotFound(NotFoundException e) {
    log.info("Not found", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {NotImplementedException.class})
  ResponseEntity<RestErrorResponse> handleNotImplemented(NotImplementedException e) {
    log.error("Not implemented", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(value = {Exception.class})
  ResponseEntity<RestErrorResponse> handleDefault(Exception e) {
    log.error("Internal error", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {AuthenticationException.class})
  ResponseEntity<RestErrorResponse> handleAuthentication(AuthenticationException e) {
    log.info("Authentication failed", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  ResponseEntity<RestErrorResponse> handleAccessDenied(AccessDeniedException e) {
    log.info("Access denied", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
  }

  private RestErrorResponse toRest(Exception e, HttpStatus status) {
    return RestErrorResponse.builder().type(status.toString()).message(e.getMessage()).build();
  }
}
