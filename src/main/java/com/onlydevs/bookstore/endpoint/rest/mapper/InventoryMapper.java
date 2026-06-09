package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.endpoint.rest.model.InventoryItemResponse;
import com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementResponse;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

  public InventoryItemResponse toRest(InventoryItem item) {
    var edition = item.getBookEdition();
    var book = edition.getBook();
    var store = item.getBookStore();

    return new InventoryItemResponse()
        .id(item.getId())
        .storeId(store.getId())
        .storeName(store.getName())
        .editionId(edition.getId())
        .bookTitle(book.getTitle())
        .isbn(edition.getIsbn())
        .quantityOnHand(item.getQuantityOnHand())
        .reorderLevel(item.getReorderLevel())
        .lowStock(item.getQuantityOnHand() <= item.getReorderLevel())
        .updatedAt(item.getUpdatedAt());
  }

  public List<InventoryItemResponse> toRestList(List<InventoryItem> items) {
    return items.stream().map(this::toRest).toList();
  }

  public InventoryMovementResponse toMovementRest(InventoryMovement movement) {
    var edition = movement.getBookEdition();
    var book = edition.getBook();

    return new InventoryMovementResponse()
        .id(movement.getId())
        .storeId(movement.getBookStore().getId())
        .editionId(edition.getId())
        .bookTitle(book.getTitle())
        .isbn(edition.getIsbn())
        .type(toDtoType(movement.getInventoryMovementType()))
        .quantity(movement.getQuantity())
        .signedQuantity(computeSignedQuantity(movement))
        .reason(movement.getReason())
        .reference(movement.getReference())
        .movedAt(movement.getMovedAt())
        .createdAt(movement.getMovedAt());
  }

  public List<InventoryMovementResponse> toMovementRestList(List<InventoryMovement> movements) {
    return movements.stream().map(this::toMovementRest).toList();
  }

  private Integer computeSignedQuantity(InventoryMovement movement) {
    return switch (movement.getInventoryMovementType()) {
      case ARRIVAL, RETURN -> movement.getQuantity();
      case SALE, DAMAGED, LOST -> -movement.getQuantity();
      case ADJUSTMENT -> movement.getQuantity();
    };
  }

  private com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementType toDtoType(
      InventoryMovementType jpaType) {
    return com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementType.fromValue(
        jpaType.name());
  }
}
