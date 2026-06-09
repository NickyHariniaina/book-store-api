package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.endpoint.rest.model.AdjustStockRequest;
import com.onlydevs.bookstore.endpoint.rest.model.ArrivalRequest;
import com.onlydevs.bookstore.endpoint.rest.model.InventoryItemResponse;
import com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementResponse;
import com.onlydevs.bookstore.endpoint.rest.model.StockLossRequest;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.InventoryService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class InventoryController {

  private final InventoryService service;
  private final InventoryMapper mapper;

  @GetMapping("/api/v1/stores/{storeId}/inventory")
  public List<InventoryItemResponse> getInventory(@PathVariable UUID storeId) {
    return mapper.toRestList(service.getInventoryByStore(storeId));
  }

  @GetMapping("/api/v1/stores/{storeId}/inventory/{editionId}")
  public InventoryItemResponse getStockByEdition(
      @PathVariable UUID storeId, @PathVariable UUID editionId) {
    return mapper.toRest(service.getStockByEdition(storeId, editionId));
  }

  @PostMapping("/api/v1/stores/{storeId}/inventory/arrival")
  public ResponseEntity<InventoryItemResponse> recordArrival(
      @PathVariable UUID storeId, @RequestBody ArrivalRequest request) {
    var item =
        service.recordArrival(
            storeId, request.getEditionId(), request.getQuantity(), request.getReference());
    return ResponseEntity.status(HttpStatus.OK).body(mapper.toRest(item));
  }

  @PostMapping("/api/v1/stores/{storeId}/inventory/adjustment")
  public ResponseEntity<InventoryItemResponse> adjustStock(
      @PathVariable UUID storeId, @RequestBody AdjustStockRequest request) {
    var item =
        service.adjustStock(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.OK).body(mapper.toRest(item));
  }

  @PostMapping("/api/v1/stores/{storeId}/inventory/damaged")
  public ResponseEntity<InventoryItemResponse> recordDamaged(
      @PathVariable UUID storeId, @RequestBody StockLossRequest request) {
    var item =
        service.recordDamaged(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.OK).body(mapper.toRest(item));
  }

  @PostMapping("/api/v1/stores/{storeId}/inventory/lost")
  public ResponseEntity<InventoryItemResponse> recordLost(
      @PathVariable UUID storeId, @RequestBody StockLossRequest request) {
    var item =
        service.recordLost(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.OK).body(mapper.toRest(item));
  }

  @GetMapping("/api/v1/stores/{storeId}/movements")
  public List<InventoryMovementResponse> getMovements(
      @PathVariable UUID storeId, @RequestParam(name = "type", required = false) String type) {
    InventoryMovementType jpaType = null;
    if (type != null) {
      try {
        jpaType = InventoryMovementType.valueOf(type);
      } catch (IllegalArgumentException e) {
        throw new NotFoundException("Unknown inventory movement type: " + type);
      }
    }
    return mapper.toMovementRestList(service.getMovements(storeId, jpaType));
  }
}
