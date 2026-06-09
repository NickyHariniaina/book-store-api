package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotImplementedException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class InventoryService {

  private final InventoryItemRepository itemRepository;
  private final InventoryMovementRepository movementRepository;

  // TODO: inject BookStoreRepository when available
  // private final BookStoreRepository bookStoreRepository;

  public List<InventoryItem> getInventoryByStore(UUID storeId) {
    return itemRepository.findByBookStoreId(storeId);
  }

  public InventoryItem getStockByEdition(UUID storeId, UUID editionId) {
    return itemRepository
        .findByBookStoreIdAndBookEditionId(storeId, editionId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Stock not found for store " + storeId + " and edition " + editionId));
  }

  @Transactional
  public InventoryItem recordArrival(
      UUID storeId, UUID editionId, Integer quantity, String reference) {
    // Try to find existing stock item
    var optItem = itemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId);

    if (optItem.isPresent()) {
      InventoryItem item = optItem.get();
      item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
      itemRepository.save(item);

      createMovement(
          item.getBookStore(),
          item.getBookEdition(),
          InventoryMovementType.ARRIVAL,
          quantity,
          "Arrival",
          reference);

      return item;
    }

    // TODO: implement creation of new InventoryItem
    // Requires BookStoreRepository and BookEditionRepository to resolve references
    throw new NotImplementedException(
        "Cannot create new stock entry for a new edition: waiting for BookStoreRepository");
  }

  @Transactional
  public InventoryItem adjustStock(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = getStockByEdition(storeId, editionId);

    int newQuantity = item.getQuantityOnHand() + quantity;
    if (newQuantity < 0) {
      throw new BadRequestException(
          "Insufficient stock: current="
              + item.getQuantityOnHand()
              + ", adjustment="
              + quantity);
    }
    item.setQuantityOnHand(newQuantity);
    itemRepository.save(item);

    String movementReason = (reason != null) ? reason : "Stock adjustment";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.ADJUSTMENT,
        Math.abs(quantity),
        movementReason,
        null);

    return item;
  }

  @Transactional
  public InventoryItem recordDamaged(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = getStockByEdition(storeId, editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as damaged";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.DAMAGED,
        quantity,
        movementReason,
        null);

    return item;
  }

  @Transactional
  public InventoryItem recordLost(
      UUID storeId, UUID editionId, Integer quantity, String reason) {
    InventoryItem item = getStockByEdition(storeId, editionId);
    applyStockDecrement(item, quantity);

    String movementReason = (reason != null) ? reason : "Marked as lost";
    createMovement(
        item.getBookStore(),
        item.getBookEdition(),
        InventoryMovementType.LOST,
        quantity,
        movementReason,
        null);

    return item;
  }

  public List<InventoryMovement> getMovements(UUID storeId, InventoryMovementType type) {
    if (type == null) {
      return movementRepository.findByBookStoreId(storeId);
    }
    return movementRepository.findByBookStoreIdAndInventoryMovementType(storeId, type);
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
    itemRepository.save(item);
  }

  private void createMovement(
      com.onlydevs.bookstore.model.BookStore bookStore,
      com.onlydevs.bookstore.model.BookEdition bookEdition,
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

    movementRepository.save(movement);
  }
}
