package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
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
  private final BookStoreRepository bookStoreRepository;
  private final BookEditionRepository bookEditionRepository;

  @Transactional
  public InventoryItem recordArrival(
      UUID storeId, UUID editionId, Integer quantity, String reference) {
    var optItem = inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId);

    if (optItem.isPresent()) {
      InventoryItem item = optItem.get();
      item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
      inventoryItemRepository.save(item);

      createMovement(
          item.getBookStore(),
          item.getBookEdition(),
          InventoryMovementType.ARRIVAL,
          quantity,
          "Arrival",
          reference);

      return item;
    }

    var storeRef = bookStoreRepository.getReferenceById(storeId);
    var editionRef = bookEditionRepository.getReferenceById(editionId);

    InventoryItem newItem =
        InventoryItem.builder()
            .bookStore(storeRef)
            .bookEdition(editionRef)
            .quantityOnHand(quantity)
            .build();

    inventoryItemRepository.save(newItem);

    createMovement(
        storeRef, editionRef, InventoryMovementType.ARRIVAL, quantity, "Arrival", reference);

    return newItem;
  }

  private InventoryItem findItem(UUID storeId, UUID editionId) {
    return inventoryItemRepository
        .findByBookStoreIdAndBookEditionId(storeId, editionId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Stock not found for store " + storeId + " and edition " + editionId));
  }

  @Transactional
  public InventoryItem adjustStock(UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = findItem(storeId, editionId);

    int newQuantity = item.getQuantityOnHand() + quantity;
    if (newQuantity < 0) {
      throw new BadRequestException(
          "Insufficient stock: current=" + item.getQuantityOnHand() + ", adjustment=" + quantity);
    }
    item.setQuantityOnHand(newQuantity);
    inventoryItemRepository.save(item);

    String movementReason = (reason != null && !reason.isBlank()) ? reason : "Stock adjustment";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.ADJUSTMENT,
        quantity,
        movementReason,
        "ADJ-" + UUID.randomUUID());

    return item;
  }

  @Transactional
  public InventoryItem recordDamaged(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = findItem(storeId, editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as damaged";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.DAMAGED,
        quantity,
        movementReason,
        "DAM-" + UUID.randomUUID());

    return item;
  }

  @Transactional
  public InventoryItem recordLost(UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = findItem(storeId, editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as lost";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.LOST,
        quantity,
        movementReason,
        "LOST-" + UUID.randomUUID());

    return item;
  }

  public Integer getEditionStock(UUID editionId) {
    return inventoryItemRepository.findByBookEditionId(editionId).stream()
        .mapToInt(InventoryItem::getQuantityOnHand)
        .sum();
  }

  public List<InventoryMovement> getMovementsByEdition(UUID editionId) {
    return inventoryMovementRepository.findByBookEditionId(editionId);
  }

  public List<InventoryMovement> getMovements(UUID storeId, InventoryMovementType type) {
    if (type == null) {
      return inventoryMovementRepository.findByBookStoreId(storeId);
    }
    return inventoryMovementRepository.findByBookStoreIdAndInventoryMovementType(storeId, type);
  }

  public List<InventoryMovement> getMovements(UUID storeId, String type) {
    InventoryMovementType jpaType = null;
    if (type != null) {
      try {
        jpaType = InventoryMovementType.valueOf(type);
      } catch (IllegalArgumentException e) {
        throw new BadRequestException("Unknown inventory movement type: " + type);
      }
    }
    return getMovements(storeId, jpaType);
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
      BookStore bookStore,
      BookEdition bookEdition,
      InventoryMovementType type,
      Integer quantity,
      String reason,
      String reference) {
    InventoryMovement movement =
        InventoryMovement.builder()
            .bookStore(bookStore)
            .bookEdition(bookEdition)
            .inventoryMovementType(type)
            .quantity(quantity)
            .reason(reason)
            .reference(reference)
            .build();

    inventoryMovementRepository.save(movement);
  }
}
