package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.NotImplementedException;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InventoryService {

  private final InventoryItemRepository itemRepository;
  private final InventoryMovementRepository movementRepository;

  public List<InventoryItem> getInventoryByStore(UUID storeId) {
    throw new NotImplementedException("Not yet implemented");
  }

  public InventoryItem getStockByEdition(UUID storeId, UUID editionId) {
    throw new NotImplementedException("Not yet implemented");
  }

  public InventoryItem recordArrival(
      UUID storeId, UUID editionId, Integer quantity, String reference) {
    throw new NotImplementedException("Not yet implemented");
  }

  public InventoryItem adjustStock(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    throw new NotImplementedException("Not yet implemented");
  }

  public InventoryItem recordDamaged(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    throw new NotImplementedException("Not yet implemented");
  }

  public InventoryItem recordLost(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    throw new NotImplementedException("Not yet implemented");
  }

  public List<InventoryMovement> getMovements(UUID storeId, InventoryMovementType type) {
    throw new NotImplementedException("Not yet implemented");
  }
}
