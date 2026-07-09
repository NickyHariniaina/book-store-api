package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.InventoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;
  private final InventoryMapper inventoryMapper;

  @PostMapping("/inventory/arrival")
  public ResponseEntity<InventoryItemResponse> recordArrival(
      @RequestParam UUID editionId,
      @RequestParam Integer quantity,
      @RequestParam(required = false) String reference) {
    var item = inventoryService.recordArrival(editionId, quantity, reference);
    return ResponseEntity.ok(inventoryMapper.toRest(item));
  }

  @PostMapping("/inventory/adjustment")
  public ResponseEntity<InventoryItemResponse> adjustStock(
      @RequestParam UUID editionId,
      @RequestParam Integer quantity,
      @RequestParam(required = false) String reason) {
    var item = inventoryService.adjustStock(editionId, quantity, reason);
    return ResponseEntity.ok(inventoryMapper.toRest(item));
  }

  @PostMapping("/inventory/damaged")
  public ResponseEntity<InventoryItemResponse> recordDamaged(
      @RequestParam UUID editionId,
      @RequestParam Integer quantity,
      @RequestParam(required = false) String reason) {
    var item = inventoryService.recordDamaged(editionId, quantity, reason);
    return ResponseEntity.ok(inventoryMapper.toRest(item));
  }

  @PostMapping("/inventory/lost")
  public ResponseEntity<InventoryItemResponse> recordLost(
      @RequestParam UUID editionId,
      @RequestParam Integer quantity,
      @RequestParam(required = false) String reason) {
    var item = inventoryService.recordLost(editionId, quantity, reason);
    return ResponseEntity.ok(inventoryMapper.toRest(item));
  }

  @GetMapping("/inventory/low-stock")
  public ResponseEntity<List<InventoryItemResponse>> getLowStock() {
    var items = inventoryService.getAllLowStock();
    return ResponseEntity.ok(inventoryMapper.toRestList(items));
  }

  @GetMapping("/editions/{editionId}/stock")
  public ResponseEntity<Integer> getEditionStock(@PathVariable UUID editionId) {
    return ResponseEntity.ok(inventoryService.getEditionStock(editionId));
  }

  @GetMapping("/editions/{editionId}/movements")
  public ResponseEntity<List<InventoryMovementResponse>> getMovementsByEdition(
      @PathVariable UUID editionId) {
    return ResponseEntity.ok(
        inventoryMapper.toMovementRestList(inventoryService.getMovementsByEdition(editionId)));
  }
}
