package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.service.SaleService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SaleController {

  private final SaleService saleService;

  @PostMapping("/sales")
  public ResponseEntity<SaleResponse> createSale(@RequestParam(required = false) UUID customerId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(customerId));
  }

  @GetMapping("/sales")
  public ResponseEntity<Page<SaleResponse>> getAllSales(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    var direction = Direction.fromString(sortDir);
    var sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(saleService.getAllSales(pageable));
  }

  @GetMapping("/sales/{id}")
  public ResponseEntity<SaleResponse> getSale(@PathVariable UUID id) {
    return ResponseEntity.ok(saleService.getSale(id));
  }

  @PatchMapping("/sales/{id}/confirm")
  public ResponseEntity<SaleResponse> confirmSale(
      @PathVariable UUID id, @RequestParam PaymentMethod paymentMethod) {
    return ResponseEntity.ok(saleService.confirmSale(id, paymentMethod));
  }

  @PatchMapping("/sales/{id}/cancel")
  public ResponseEntity<SaleResponse> cancelSale(@PathVariable UUID id) {
    return ResponseEntity.ok(saleService.cancelSale(id));
  }

  @PatchMapping("/sales/{id}/refund")
  public ResponseEntity<SaleResponse> refundSale(@PathVariable UUID id) {
    return ResponseEntity.ok(saleService.refundSale(id));
  }
}
