package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.endpoint.rest.model.AdjustStockRequest;
import com.onlydevs.bookstore.endpoint.rest.model.ArrivalRequest;
import com.onlydevs.bookstore.endpoint.rest.model.InventoryItemResponse;
import com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementResponse;
import com.onlydevs.bookstore.endpoint.rest.model.StockLossRequest;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.service.InventoryService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class InventoryControllerTest {

  private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID EDITION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

  private final InventoryService service = mock(InventoryService.class);
  private final InventoryMapper mapper = mock(InventoryMapper.class);
  private final InventoryController controller = new InventoryController(service, mapper);

  private BookStore store;
  private BookEdition edition;
  private InventoryItem item;
  private InventoryItemResponse itemResponse;
  private InventoryMovement movement;
  private InventoryMovementResponse movementResponse;

  @BeforeEach
  void setUp() {
    store = BookStore.builder().id(STORE_ID).name("Test Store").build();
    edition = BookEdition.builder().id(EDITION_ID).isbn("1234567890").build();
    item =
        InventoryItem.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000003"))
            .bookStore(store)
            .bookEdition(edition)
            .quantityOnHand(10)
            .reorderLevel(5)
            .build();
    itemResponse =
        new InventoryItemResponse()
            .id(item.getId())
            .storeId(STORE_ID)
            .storeName("Test Store")
            .editionId(EDITION_ID)
            .bookTitle("Test Book")
            .isbn("1234567890")
            .quantityOnHand(10)
            .reorderLevel(5)
            .lowStock(false)
            .updatedAt(Instant.now());
    movement =
        InventoryMovement.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000004"))
            .bookStore(store)
            .bookEdition(edition)
            .inventoryMovementType(InventoryMovementType.ARRIVAL)
            .quantity(5)
            .reason("Arrival")
            .movedAt(Instant.now())
            .build();
    movementResponse =
        new InventoryMovementResponse()
            .id(movement.getId())
            .storeId(STORE_ID)
            .editionId(EDITION_ID)
            .bookTitle("Test Book")
            .isbn("1234567890")
            .type(
                com.onlydevs.bookstore.endpoint.rest.model.InventoryMovementType.ARRIVAL)
            .quantity(5)
            .signedQuantity(5)
            .reason("Arrival")
            .movedAt(Instant.now())
            .createdAt(Instant.now());
  }

  @Test
  void getInventory_returnsList() {
    when(service.getInventoryByStore(STORE_ID)).thenReturn(List.of(item));
    when(mapper.toRestList(List.of(item))).thenReturn(List.of(itemResponse));

    var result = controller.getInventory(STORE_ID);

    assertEquals(1, result.size());
    assertEquals(10, result.get(0).getQuantityOnHand());
  }

  @Test
  void getStockByEdition_found_returnsItem() {
    when(service.getStockByEdition(STORE_ID, EDITION_ID)).thenReturn(item);
    when(mapper.toRest(item)).thenReturn(itemResponse);

    var result = controller.getStockByEdition(STORE_ID, EDITION_ID);

    assertEquals(10, result.getQuantityOnHand());
  }

  @Test
  void getStockByEdition_notFound_throws() {
    when(service.getStockByEdition(STORE_ID, EDITION_ID))
        .thenThrow(new NotFoundException("Not found"));

    assertThrows(
        NotFoundException.class, () -> controller.getStockByEdition(STORE_ID, EDITION_ID));
  }

  @Test
  void recordArrival_returnsOk() {
    var request = new ArrivalRequest().editionId(EDITION_ID).quantity(5).reference("REF-001");
    when(service.recordArrival(STORE_ID, EDITION_ID, 5, "REF-001")).thenReturn(item);
    when(mapper.toRest(item)).thenReturn(itemResponse);

    var result = controller.recordArrival(STORE_ID, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(10, result.getBody().getQuantityOnHand());
  }

  @Test
  void adjustStock_returnsOk() {
    var request = new AdjustStockRequest().editionId(EDITION_ID).quantity(5).reason("Add stock");
    when(service.adjustStock(STORE_ID, EDITION_ID, 5, "Add stock")).thenReturn(item);
    when(mapper.toRest(item)).thenReturn(itemResponse);

    var result = controller.adjustStock(STORE_ID, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void recordDamaged_returnsOk() {
    var request = new StockLossRequest().editionId(EDITION_ID).quantity(2).reason("Torn cover");
    when(service.recordDamaged(STORE_ID, EDITION_ID, 2, "Torn cover")).thenReturn(item);
    when(mapper.toRest(item)).thenReturn(itemResponse);

    var result = controller.recordDamaged(STORE_ID, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void recordLost_returnsOk() {
    var request = new StockLossRequest().editionId(EDITION_ID).quantity(1).reason("Misplaced");
    when(service.recordLost(STORE_ID, EDITION_ID, 1, "Misplaced")).thenReturn(item);
    when(mapper.toRest(item)).thenReturn(itemResponse);

    var result = controller.recordLost(STORE_ID, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void getMovements_withoutType_returnsAll() {
    when(service.getMovements(STORE_ID, null)).thenReturn(List.of(movement));
    when(mapper.toMovementRestList(List.of(movement))).thenReturn(List.of(movementResponse));

    var result = controller.getMovements(STORE_ID, null);

    assertEquals(1, result.size());
  }

  @Test
  void getMovements_withValidType_filters() {
    when(service.getMovements(STORE_ID, InventoryMovementType.ARRIVAL))
        .thenReturn(List.of(movement));
    when(mapper.toMovementRestList(List.of(movement))).thenReturn(List.of(movementResponse));

    var result = controller.getMovements(STORE_ID, "ARRIVAL");

    assertEquals(1, result.size());
  }

  @Test
  void getMovements_withInvalidType_throws() {
    assertThrows(
        NotFoundException.class, () -> controller.getMovements(STORE_ID, "INVALID"));
  }
}
