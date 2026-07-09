package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryMapperTest {

  private final InventoryMapper inventoryMapper = new InventoryMapper();

  private UUID editionId;
  private UUID itemId;
  private UUID movementId;
  private Book book;
  private BookEdition edition;
  private InventoryItem item;
  private InventoryMovement movement;

  @BeforeEach
  void setUp() {
    editionId = UUID.randomUUID();
    itemId = UUID.randomUUID();
    movementId = UUID.randomUUID();

    book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Test Book")
            .language(BookLanguage.ENGLISH)
            .build();

    edition =
        BookEdition.builder()
            .id(editionId)
            .isbn("1234567890")
            .format(BookFormat.PAPERBACK)
            .book(book)
            .build();

    item =
        InventoryItem.builder()
            .id(itemId)
            .bookEdition(edition)
            .quantityOnHand(10)
            .reorderLevel(5)
            .updatedAt(Instant.now())
            .build();

    movement =
        InventoryMovement.builder()
            .id(movementId)
            .bookEdition(edition)
            .inventoryMovementType(InventoryMovementType.ARRIVAL)
            .quantity(5)
            .reason("Initial stock")
            .reference("REF-001")
            .movedAt(Instant.now())
            .build();
  }

  @Test
  void to_rest_should_map_inventory_item_to_response() {
    InventoryItemResponse result = inventoryMapper.toRest(item);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemId);
    assertThat(result.getEditionId()).isEqualTo(editionId);
    assertThat(result.getBookTitle()).isEqualTo("Test Book");
    assertThat(result.getIsbn()).isEqualTo("1234567890");
    assertThat(result.getQuantityOnHand()).isEqualTo(10);
    assertThat(result.getReorderLevel()).isEqualTo(5);
    assertThat(result.getLowStock()).isFalse();
    assertThat(result.getUpdatedAt()).isNotNull();
  }

  @Test
  void to_rest_should_return_null_when_item_is_null() {
    assertThat(inventoryMapper.toRest(null)).isNull();
  }

  @Test
  void to_rest_should_detect_low_stock() {
    item.setQuantityOnHand(3);
    item.setReorderLevel(5);

    InventoryItemResponse result = inventoryMapper.toRest(item);

    assertThat(result.getLowStock()).isTrue();
  }

  @Test
  void to_rest_list_should_map_list() {
    var results = inventoryMapper.toRestList(java.util.List.of(item));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(itemId);
  }

  @Test
  void to_movement_rest_should_map_movement_to_response() {
    InventoryMovementResponse result = inventoryMapper.toMovementRest(movement);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(movementId);
    assertThat(result.getEditionId()).isEqualTo(editionId);
    assertThat(result.getBookTitle()).isEqualTo("Test Book");
    assertThat(result.getIsbn()).isEqualTo("1234567890");
    assertThat(result.getType()).isEqualTo("ARRIVAL");
    assertThat(result.getQuantity()).isEqualTo(5);
    assertThat(result.getSignedQuantity()).isEqualTo(5);
    assertThat(result.getReason()).isEqualTo("Initial stock");
    assertThat(result.getReference()).isEqualTo("REF-001");
    assertThat(result.getMovedAt()).isNotNull();
  }

  @Test
  void to_movement_rest_should_return_null_when_movement_is_null() {
    assertThat(inventoryMapper.toMovementRest(null)).isNull();
  }

  @Test
  void to_movement_rest_should_compute_negative_signed_quantity_for_loss() {
    movement.setInventoryMovementType(InventoryMovementType.DAMAGED);

    InventoryMovementResponse result = inventoryMapper.toMovementRest(movement);

    assertThat(result.getSignedQuantity()).isNegative();
  }

  @Test
  void to_movement_rest_list_should_map_list() {
    var results = inventoryMapper.toMovementRestList(java.util.List.of(movement));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(movementId);
  }
}
