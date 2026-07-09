package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.InventoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;
  private final InventoryMapper inventoryMapper;

  @GetMapping("/editions/{editionId}/movements")
  public ResponseEntity<List<InventoryMovementResponse>> getMovementsByEdition(
      @PathVariable UUID editionId) {
    return ResponseEntity.ok(
        inventoryMapper.toMovementRestList(inventoryService.getMovementsByEdition(editionId)));
  }
}
