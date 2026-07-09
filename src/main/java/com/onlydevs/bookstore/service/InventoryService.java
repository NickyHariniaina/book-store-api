package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

  private final InventoryItemRepository inventoryItemRepository;
  private final InventoryMovementRepository inventoryMovementRepository;
  private final BookEditionRepository bookEditionRepository;

  @Transactional
  public InventoryItem recordArrival(UUID editionId, Integer quantity, String reference) {
    var optItem = inventoryItemRepository.findByBookEditionId(editionId);

    if (optItem.isPresent()) {
      var item = optItem.get();
      item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
      inventoryItemRepository.save(item);

      createMovement(
          item.getBookEdition(), InventoryMovementType.ARRIVAL, quantity, "Arrival", reference);

      return item;
    }

    var editionRef = bookEditionRepository.getReferenceById(editionId);

    var newItem = InventoryItem.builder().bookEdition(editionRef).quantityOnHand(quantity).build();

    inventoryItemRepository.save(newItem);

    createMovement(editionRef, InventoryMovementType.ARRIVAL, quantity, "Arrival", reference);

    return newItem;
  }

  private InventoryItem findItem(UUID editionId) {
    return inventoryItemRepository
        .findByBookEditionId(editionId)
        .orElseThrow(() -> new NotFoundException("Stock not found for edition " + editionId));
  }

  @Transactional
  public InventoryItem adjustStock(UUID editionId, Integer quantity, String reason) {
    var item = findItem(editionId);

    int newQuantity = item.getQuantityOnHand() + quantity;
    if (newQuantity < 0) {
      throw new BadRequestException(
          "Insufficient stock: current=" + item.getQuantityOnHand() + ", adjustment=" + quantity);
    }
    item.setQuantityOnHand(newQuantity);
    inventoryItemRepository.save(item);

    String movementReason = (reason != null && !reason.isBlank()) ? reason : "Stock adjustment";
    createMovement(
        item.getBookEdition(),
        InventoryMovementType.ADJUSTMENT,
        quantity,
        movementReason,
        "ADJ-" + UUID.randomUUID());

    return item;
  }

  @Transactional
  public InventoryItem recordDamaged(UUID editionId, Integer quantity, String reason) {
    var item = findItem(editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as damaged";
    createMovement(
        item.getBookEdition(),
        InventoryMovementType.DAMAGED,
        quantity,
        movementReason,
        "DAM-" + UUID.randomUUID());

    return item;
  }

  @Transactional
  public InventoryItem recordLost(UUID editionId, Integer quantity, String reason) {
    var item = findItem(editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as lost";
    createMovement(
        item.getBookEdition(),
        InventoryMovementType.LOST,
        quantity,
        movementReason,
        "LOST-" + UUID.randomUUID());

    return item;
  }

  public Integer getEditionStock(UUID editionId) {
    return inventoryItemRepository
        .findByBookEditionId(editionId)
        .map(InventoryItem::getQuantityOnHand)
        .orElse(0);
  }

  public List<InventoryMovement> getMovementsByEdition(UUID editionId) {
    return inventoryMovementRepository.findByBookEditionId(editionId);
  }

  public List<InventoryItem> getAllLowStock() {
    return inventoryItemRepository.findAllLowStock();
  }

  private void applyStockDecrement(InventoryItem item, Integer quantity) {
    int newQuantity = item.getQuantityOnHand() - quantity;
    if (newQuantity < 0) {
      throw new BadRequestException(
          "Insufficient stock: current="
              + item.getQuantityOnHand()
              + ", requested decrease="
              + quantity);
    }
    item.setQuantityOnHand(newQuantity);
    inventoryItemRepository.save(item);
  }

  private void createMovement(
      BookEdition bookEdition,
      InventoryMovementType type,
      Integer quantity,
      String reason,
      String reference) {
    var movement =
        InventoryMovement.builder()
            .bookEdition(bookEdition)
            .inventoryMovementType(type)
            .quantity(quantity)
            .reason(reason)
            .reference(reference)
            .build();

    inventoryMovementRepository.save(movement);
  }
}
