package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

  public InventoryItemResponse toRest(InventoryItem item) {
    if (item == null) {
      return null;
    }
    var edition = item.getBookEdition();
    var book = edition.getBook();

    return InventoryItemResponse.builder()
        .id(item.getId())
        .editionId(edition.getId())
        .bookTitle(book.getTitle())
        .isbn(edition.getIsbn())
        .quantityOnHand(item.getQuantityOnHand())
        .reorderLevel(item.getReorderLevel())
        .lowStock(item.getQuantityOnHand() <= item.getReorderLevel())
        .updatedAt(item.getUpdatedAt())
        .build();
  }

  public List<InventoryItemResponse> toRestList(List<InventoryItem> items) {
    return items.stream().map(this::toRest).toList();
  }

  public InventoryMovementResponse toMovementRest(InventoryMovement movement) {
    if (movement == null) {
      return null;
    }
    var edition = movement.getBookEdition();
    var book = edition.getBook();

    return InventoryMovementResponse.builder()
        .id(movement.getId())
        .editionId(edition.getId())
        .bookTitle(book.getTitle())
        .isbn(edition.getIsbn())
        .type(movement.getInventoryMovementType().name())
        .quantity(movement.getQuantity())
        .signedQuantity(computeSignedQuantity(movement))
        .reason(movement.getReason())
        .reference(movement.getReference())
        .movedAt(movement.getMovedAt())
        .createdAt(movement.getMovedAt())
        .build();
  }

  public List<InventoryMovementResponse> toMovementRestList(List<InventoryMovement> movements) {
    return movements.stream().map(this::toMovementRest).toList();
  }

  private Integer computeSignedQuantity(InventoryMovement movement) {
    return switch (movement.getInventoryMovementType()) {
      case ARRIVAL, ADJUSTMENT -> movement.getQuantity();
      case SALE, DAMAGED, LOST -> -movement.getQuantity();
    };
  }
}
