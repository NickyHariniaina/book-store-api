package com.onlydevs.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.exception.NotImplementedException;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {

  private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID EDITION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

  private final InventoryItemRepository itemRepository = mock(InventoryItemRepository.class);
  private final InventoryMovementRepository movementRepository =
      mock(InventoryMovementRepository.class);
  private final InventoryService subject = new InventoryService(itemRepository, movementRepository);

  private BookStore store;
  private BookEdition edition;
  private InventoryItem item;

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
  }

  @Test
  void getInventoryByStore_returnsList() {
    when(itemRepository.findByBookStoreId(STORE_ID)).thenReturn(List.of(item));

    var result = subject.getInventoryByStore(STORE_ID);

    assertEquals(1, result.size());
    assertEquals(10, result.get(0).getQuantityOnHand());
  }

  @Test
  void getStockByEdition_found_returnsItem() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.getStockByEdition(STORE_ID, EDITION_ID);

    assertEquals(10, result.getQuantityOnHand());
  }

  @Test
  void getStockByEdition_notFound_throws() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> subject.getStockByEdition(STORE_ID, EDITION_ID));
  }

  @Test
  void recordArrival_existingItem_incrementsStock() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.recordArrival(STORE_ID, EDITION_ID, 5, "REF-001");

    assertEquals(15, result.getQuantityOnHand());
    verify(itemRepository).save(item);
    verify(movementRepository).save(any(InventoryMovement.class));
  }

  @Test
  void recordArrival_newItem_throwsNotImplemented() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        NotImplementedException.class,
        () -> subject.recordArrival(STORE_ID, EDITION_ID, 5, "REF-001"));
    verify(movementRepository, never()).save(any());
  }

  @Test
  void adjustStock_increments() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.adjustStock(STORE_ID, EDITION_ID, 3, "Add stock");

    assertEquals(13, result.getQuantityOnHand());
    verify(itemRepository).save(item);
    verify(movementRepository).save(any(InventoryMovement.class));
  }

  @Test
  void adjustStock_decrements() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.adjustStock(STORE_ID, EDITION_ID, -3, "Remove stock");

    assertEquals(7, result.getQuantityOnHand());
    verify(itemRepository).save(item);
    verify(movementRepository).save(any(InventoryMovement.class));
  }

  @Test
  void adjustStock_insufficientStock_throws() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    assertThrows(
        BadRequestException.class,
        () -> subject.adjustStock(STORE_ID, EDITION_ID, -20, "Too much"));
    verify(movementRepository, never()).save(any());
  }

  @Test
  void adjustStock_itemNotFound_throws() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> subject.adjustStock(STORE_ID, EDITION_ID, 5, "Not found"));
  }

  @Test
  void recordDamaged_decrements() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.recordDamaged(STORE_ID, EDITION_ID, 3, "Torn cover");

    assertEquals(7, result.getQuantityOnHand());
    verify(movementRepository).save(any(InventoryMovement.class));
  }

  @Test
  void recordDamaged_insufficientStock_throws() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    assertThrows(
        BadRequestException.class,
        () -> subject.recordDamaged(STORE_ID, EDITION_ID, 20, "Too many damaged"));
  }

  @Test
  void recordLost_decrements() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    var result = subject.recordLost(STORE_ID, EDITION_ID, 2, "Misplaced");

    assertEquals(8, result.getQuantityOnHand());
    verify(movementRepository).save(any(InventoryMovement.class));
  }

  @Test
  void recordLost_insufficientStock_throws() {
    when(itemRepository.findByBookStoreIdAndBookEditionId(STORE_ID, EDITION_ID))
        .thenReturn(Optional.of(item));

    assertThrows(
        BadRequestException.class,
        () -> subject.recordLost(STORE_ID, EDITION_ID, 20, "Too many lost"));
  }

  @Test
  void getMovements_withoutType_returnsAll() {
    when(movementRepository.findByBookStoreId(STORE_ID)).thenReturn(List.of());

    var result = subject.getMovements(STORE_ID, null);

    assertEquals(0, result.size());
    verify(movementRepository).findByBookStoreId(STORE_ID);
  }

  @Test
  void getMovements_withType_filters() {
    when(movementRepository.findByBookStoreIdAndInventoryMovementType(
            STORE_ID, InventoryMovementType.ARRIVAL))
        .thenReturn(List.of());

    var result = subject.getMovements(STORE_ID, InventoryMovementType.ARRIVAL);

    assertEquals(0, result.size());
    verify(movementRepository)
        .findByBookStoreIdAndInventoryMovementType(STORE_ID, InventoryMovementType.ARRIVAL);
  }
}
