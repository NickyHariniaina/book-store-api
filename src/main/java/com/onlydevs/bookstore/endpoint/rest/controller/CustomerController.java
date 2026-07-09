package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import com.onlydevs.bookstore.service.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
  private final CustomerService customerService;

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
    return ResponseEntity.ok(customerService.getAllCustomers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
    return ResponseEntity.ok(customerService.getCustomerById(id));
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> createCustomer(
      @Valid @RequestBody CreateCustomerRequest request) {
    CustomerResponse response = customerService.createCustomer(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.getId())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponse> updateCustomer(
      @PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
    return ResponseEntity.ok(customerService.updateCustomer(id, request));
  }

  @GetMapping("/{id}/sales")
  public ResponseEntity<List<SaleSummaryResponse>> getCustomerSales(@PathVariable UUID id) {
    return ResponseEntity.ok(customerService.getCustomerSales(id));
  }
}
