package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.onlydevs.bookstore.conf.FacadeIT;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

class CustomerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  private final String baseUri = "/api/v1/customers";

  @Test
  void customerCrud_ShouldWorkEndToEnd() {
    // Create
    CreateCustomerRequest createRequest = CreateCustomerRequest.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .phone("+123456789")
        .build();

    ResponseEntity<String> createResponse = restTemplate.postForEntity(
        baseUri, createRequest, String.class);
    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    assertNotNull(createResponse.getBody());
    assertTrue(createResponse.getBody().contains("John Doe"));
    assertTrue(createResponse.getBody().contains("john.doe@example.com"));

    // Extract ID from Location header
    String location = createResponse.getHeaders().getLocation().toString();
    UUID customerId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

    // Get by ID
    ResponseEntity<String> getResponse = restTemplate.getForEntity(
        baseUri + "/" + customerId, String.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    // Get all
    ResponseEntity<String> getAllResponse = restTemplate.getForEntity(baseUri, String.class);
    assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());

    // Update
    UpdateCustomerRequest updateRequest = UpdateCustomerRequest.builder()
        .firstName("Jane")
        .lastName("Smith")
        .build();

    ResponseEntity<String> updateResponse = restTemplate.exchange(
        RequestEntity.put(URI.create(baseUri + "/" + customerId))
            .body(updateRequest), String.class);
    assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    assertTrue(updateResponse.getBody().contains("Jane Smith"));

    // Get sales (empty)
    ResponseEntity<String> salesResponse = restTemplate.getForEntity(
        baseUri + "/" + customerId + "/sales", String.class);
    assertEquals(HttpStatus.OK, salesResponse.getStatusCode());
  }
}
