package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateBookPriceRequest;
import com.onlydevs.bookstore.model.dto.response.BookPriceResponse;
import com.onlydevs.bookstore.service.BookPriceHistoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookPriceHistoryController {
  private final BookPriceHistoryService bookPriceHistoryService;

  @GetMapping("/editions/{id}/prices")
  public ResponseEntity<List<BookPriceResponse>> getPrices(@PathVariable UUID id) {
    return ResponseEntity.ok(bookPriceHistoryService.getPrices(id));
  }

  @GetMapping("/editions/{id}/prices/current")
  public ResponseEntity<BookPriceResponse> getCurrentPrice(@PathVariable UUID id) {
    return ResponseEntity.ok(bookPriceHistoryService.getCurrentPrice(id));
  }

  @PostMapping("/editions/{id}/prices")
  public ResponseEntity<BookPriceResponse> createPrice(
      @PathVariable UUID id, @Valid @RequestBody CreateBookPriceRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookPriceHistoryService.createPrice(id, request));
  }
}
