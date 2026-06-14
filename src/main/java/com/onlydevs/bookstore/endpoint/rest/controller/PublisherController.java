package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.service.PublisherService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {

  private final PublisherService publisherService;

  @GetMapping
  public ResponseEntity<Page<PublisherResponse>> getAllPublishers(Pageable pageable) {
    return ResponseEntity.ok(publisherService.getAllPublishers(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PublisherResponse> getPublisherById(@PathVariable UUID id) {
    return ResponseEntity.ok(publisherService.getPublisherById(id));
  }

  @PostMapping
  public ResponseEntity<PublisherResponse> createPublisher(
      @RequestBody CreatePublisherRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(publisherService.createPublisher(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PublisherResponse> updatePublisher(
      @PathVariable UUID id, @RequestBody UpdatePublisherRequest request) {
    return ResponseEntity.ok(publisherService.updatePublisher(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePublisher(@PathVariable UUID id) {
    publisherService.deletePublisher(id);
    return ResponseEntity.noContent().build();
  }
}
