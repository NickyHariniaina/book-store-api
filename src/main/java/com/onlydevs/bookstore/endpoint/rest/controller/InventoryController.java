package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.request.AdjustStockRequest;
import com.onlydevs.bookstore.model.dto.request.ArrivalRequest;
import com.onlydevs.bookstore.model.dto.request.StockLossRequest;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.InventoryService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;
  private final InventoryMapper inventoryMapper;

  @PostMapping("/stores/{storeId}/inventory/arrival")
  public ResponseEntity<InventoryItemResponse> recordArrival(
      @PathVariable UUID storeId, @Valid @RequestBody ArrivalRequest request) {
    var item =
        inventoryService.recordArrival(
            storeId, request.getEditionId(), request.getQuantity(), request.getReference());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/stores/{storeId}/inventory/adjustment")
  public ResponseEntity<InventoryItemResponse> adjustStock(
      @PathVariable UUID storeId, @Valid @RequestBody AdjustStockRequest request) {
    var item =
        inventoryService.adjustStock(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/stores/{storeId}/inventory/damaged")
  public ResponseEntity<InventoryItemResponse> recordDamaged(
      @PathVariable UUID storeId, @Valid @RequestBody StockLossRequest request) {
    var item =
        inventoryService.recordDamaged(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/stores/{storeId}/inventory/lost")
  public ResponseEntity<InventoryItemResponse> recordLost(
      @PathVariable UUID storeId, @Valid @RequestBody StockLossRequest request) {
    var item =
        inventoryService.recordLost(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @GetMapping("/editions/{editionId}/movements")
  public ResponseEntity<List<InventoryMovementResponse>> getMovementsByEdition(
      @PathVariable UUID editionId) {
    return ResponseEntity.ok(
        inventoryMapper.toMovementRestList(inventoryService.getMovementsByEdition(editionId)));
  }

  @GetMapping("/stores/{storeId}/movements")
  public ResponseEntity<List<InventoryMovementResponse>> getMovements(
      @PathVariable UUID storeId, @RequestParam(name = "type", required = false) String type) {
    return ResponseEntity.ok(
        inventoryMapper.toMovementRestList(inventoryService.getMovements(storeId, type)));
  }
}
