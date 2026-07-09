package com.onlydevs.bookstore.endpoint.rest.controller;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.request.AdjustStockRequest;
import com.onlydevs.bookstore.model.dto.request.ArrivalRequest;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.StockLossRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.BookStoreService;
import com.onlydevs.bookstore.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class BookStoreController {

  private final BookStoreService bookStoreService;
  private final InventoryService inventoryService;
  private final InventoryMapper inventoryMapper;

  @GetMapping
  public ResponseEntity<Page<BookStoreResponse>> getAllStores(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    Direction direction = Direction.fromString(sortDir);
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.getAllStores(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookStoreResponse> getStoreById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.getStoreById(id));
  }

  @PostMapping
  public ResponseEntity<BookStoreResponse> createStore(
      @Valid @RequestBody CreateBookStoreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookStoreService.createStore(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookStoreResponse> updateStore(
      @PathVariable UUID id, @Valid @RequestBody UpdateBookStoreRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(bookStoreService.updateStore(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
    bookStoreService.deleteStore(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{id}/inventory")
  public ResponseEntity<List<InventoryItemResponse>> getInventory(@PathVariable UUID id) {
    var items = bookStoreService.getInventoryByStore(id);
    return ResponseEntity.ok(inventoryMapper.toRestList(items));
  }

  @GetMapping("/{id}/inventory/{editionId}")
  public ResponseEntity<InventoryItemResponse> getStockByEdition(
      @PathVariable UUID id, @PathVariable UUID editionId) {
    var item = bookStoreService.getStockByEdition(id, editionId);
    return ResponseEntity.ok(inventoryMapper.toRest(item));
  }

  @GetMapping("/{id}/books/{bookId}/stock")
  public ResponseEntity<Integer> getBookStockByStore(
      @PathVariable UUID id, @PathVariable UUID bookId) {
    return ResponseEntity.ok(bookStoreService.getBookStockByStore(id, bookId));
  }

  @PostMapping("/{storeId}/inventory/arrival")
  public ResponseEntity<InventoryItemResponse> recordArrival(
      @PathVariable UUID storeId, @Valid @RequestBody ArrivalRequest request) {
    var item =
        inventoryService.recordArrival(
            storeId, request.getEditionId(), request.getQuantity(), request.getReference());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/{storeId}/inventory/adjustment")
  public ResponseEntity<InventoryItemResponse> adjustStock(
      @PathVariable UUID storeId, @Valid @RequestBody AdjustStockRequest request) {
    var item =
        inventoryService.adjustStock(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/{storeId}/inventory/damaged")
  public ResponseEntity<InventoryItemResponse> recordDamaged(
      @PathVariable UUID storeId, @Valid @RequestBody StockLossRequest request) {
    var item =
        inventoryService.recordDamaged(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @PostMapping("/{storeId}/inventory/lost")
  public ResponseEntity<InventoryItemResponse> recordLost(
      @PathVariable UUID storeId, @Valid @RequestBody StockLossRequest request) {
    var item =
        inventoryService.recordLost(
            storeId, request.getEditionId(), request.getQuantity(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toRest(item));
  }

  @GetMapping("/{storeId}/movements")
  public ResponseEntity<List<InventoryMovementResponse>> getMovements(
      @PathVariable UUID storeId, @RequestParam(name = "type", required = false) String type) {
    return ResponseEntity.ok(
        inventoryMapper.toMovementRestList(inventoryService.getMovements(storeId, type)));
  }
}
