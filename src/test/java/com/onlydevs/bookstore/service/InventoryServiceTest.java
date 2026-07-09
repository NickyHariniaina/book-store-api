package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

  @Mock private InventoryItemRepository inventoryItemRepository;

  @Mock private InventoryMovementRepository inventoryMovementRepository;

  @Mock private BookStoreRepository bookStoreRepository;

  @Mock private BookEditionRepository bookEditionRepository;

  @InjectMocks private InventoryService inventoryService;

  private UUID storeId;
  private UUID editionId;
  private BookStore store;
  private BookEdition edition;
  private InventoryItem item;

  @BeforeEach
  void setUp() {
    storeId = UUID.randomUUID();
    editionId = UUID.randomUUID();
    store = BookStore.builder().id(storeId).name("Test Store").build();
    edition = BookEdition.builder().id(editionId).isbn("1234567890").build();
    item =
        InventoryItem.builder()
            .id(UUID.randomUUID())
            .bookStore(store)
            .bookEdition(edition)
            .quantityOnHand(10)
            .reorderLevel(5)
            .build();
  }

  @Test
  void record_arrival_when_item_exists_should_increment_stock() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    var result = inventoryService.recordArrival(storeId, editionId, 5, "REF-001");

    assertThat(result.getQuantityOnHand()).isEqualTo(15);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_arrival_when_item_not_exists_should_create_new() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.empty());
    given(bookStoreRepository.getReferenceById(storeId)).willReturn(store);
    given(bookEditionRepository.getReferenceById(editionId)).willReturn(edition);
    given(inventoryItemRepository.save(any(InventoryItem.class))).willAnswer(i -> i.getArgument(0));

    var result = inventoryService.recordArrival(storeId, editionId, 5, "REF-001");

    assertThat(result.getQuantityOnHand()).isEqualTo(5);
    then(inventoryItemRepository).should().save(any(InventoryItem.class));
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_should_increment() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    var result = inventoryService.adjustStock(storeId, editionId, 3, "Add stock");

    assertThat(result.getQuantityOnHand()).isEqualTo(13);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_should_decrement() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    var result = inventoryService.adjustStock(storeId, editionId, -3, "Remove stock");

    assertThat(result.getQuantityOnHand()).isEqualTo(7);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    assertThatThrownBy(() -> inventoryService.adjustStock(storeId, editionId, -20, "Too much"))
        .isInstanceOf(BadRequestException.class);
    then(inventoryMovementRepository).should(never()).save(any());
  }

  @Test
  void adjust_stock_when_item_not_found_should_throw() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> inventoryService.adjustStock(storeId, editionId, 5, "Not found"))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void record_damaged_should_decrement() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    var result = inventoryService.recordDamaged(storeId, editionId, 3, "Torn cover");

    assertThat(result.getQuantityOnHand()).isEqualTo(7);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_damaged_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    assertThatThrownBy(
            () -> inventoryService.recordDamaged(storeId, editionId, 20, "Too many damaged"))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void record_lost_should_decrement() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    var result = inventoryService.recordLost(storeId, editionId, 2, "Misplaced");

    assertThat(result.getQuantityOnHand()).isEqualTo(8);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_lost_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookStoreIdAndBookEditionId(storeId, editionId))
        .willReturn(Optional.of(item));

    assertThatThrownBy(() -> inventoryService.recordLost(storeId, editionId, 20, "Too many lost"))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void get_movements_by_edition_should_return_list() {
    given(inventoryMovementRepository.findByBookEditionId(editionId)).willReturn(List.of());

    var result = inventoryService.getMovementsByEdition(editionId);

    assertThat(result).isEmpty();
    then(inventoryMovementRepository).should().findByBookEditionId(editionId);
  }

  @Test
  void get_movements_without_type_should_return_all() {
    given(inventoryMovementRepository.findByBookStoreId(storeId)).willReturn(List.of());

    var result = inventoryService.getMovements(storeId, (String) null);

    assertThat(result).isEmpty();
    then(inventoryMovementRepository).should().findByBookStoreId(storeId);
  }

  @Test
  void get_movements_with_type_should_filter() {
    given(
            inventoryMovementRepository.findByBookStoreIdAndInventoryMovementType(
                storeId, InventoryMovementType.ARRIVAL))
        .willReturn(List.of());

    var result = inventoryService.getMovements(storeId, InventoryMovementType.ARRIVAL);

    assertThat(result).isEmpty();
    then(inventoryMovementRepository)
        .should()
        .findByBookStoreIdAndInventoryMovementType(storeId, InventoryMovementType.ARRIVAL);
  }

  @Test
  void getEditionStock_shouldSumAcrossAllStores() {
    InventoryItem item1 = InventoryItem.builder().quantityOnHand(5).build();
    InventoryItem item2 = InventoryItem.builder().quantityOnHand(3).build();
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(List.of(item1, item2));

    Integer stock = inventoryService.getEditionStock(editionId);

    assertThat(stock).isEqualTo(8);
    then(inventoryItemRepository).should().findByBookEditionId(editionId);
  }

  @Test
  void getEditionStock_withNoInventory_shouldReturnZero() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(List.of());

    Integer stock = inventoryService.getEditionStock(editionId);

    assertThat(stock).isEqualTo(0);
    then(inventoryItemRepository).should().findByBookEditionId(editionId);
  }
}
