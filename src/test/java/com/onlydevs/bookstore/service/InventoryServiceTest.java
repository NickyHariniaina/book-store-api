package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
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

  @Mock private BookEditionRepository bookEditionRepository;

  @Mock private BookRepository bookRepository;

  @InjectMocks private InventoryService inventoryService;

  private UUID editionId;
  private UUID bookId;
  private BookEdition edition;
  private InventoryItem item;

  @BeforeEach
  void setUp() {
    editionId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    edition = BookEdition.builder().id(editionId).isbn("1234567890").build();
    item =
        InventoryItem.builder()
            .id(UUID.randomUUID())
            .bookEdition(edition)
            .quantityOnHand(10)
            .reorderLevel(5)
            .build();
  }

  @Test
  void record_arrival_when_item_exists_should_increment_stock() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    var result = inventoryService.recordArrival(editionId, 5, "REF-001");

    assertThat(result.getQuantityOnHand()).isEqualTo(15);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_arrival_when_item_not_exists_should_create_new() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.empty());
    given(bookEditionRepository.getReferenceById(editionId)).willReturn(edition);
    given(inventoryItemRepository.save(any(InventoryItem.class))).willAnswer(i -> i.getArgument(0));

    var result = inventoryService.recordArrival(editionId, 5, "REF-001");

    assertThat(result.getQuantityOnHand()).isEqualTo(5);
    then(inventoryItemRepository).should().save(any(InventoryItem.class));
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_should_increment() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    var result = inventoryService.adjustStock(editionId, 3, "Add stock");

    assertThat(result.getQuantityOnHand()).isEqualTo(13);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_should_decrement() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    var result = inventoryService.adjustStock(editionId, -3, "Remove stock");

    assertThat(result.getQuantityOnHand()).isEqualTo(7);
    then(inventoryItemRepository).should().save(item);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void adjust_stock_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    assertThatThrownBy(() -> inventoryService.adjustStock(editionId, -20, "Too much"))
        .isInstanceOf(BadRequestException.class);
    then(inventoryMovementRepository).should(never()).save(any());
  }

  @Test
  void adjust_stock_when_item_not_found_should_throw() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> inventoryService.adjustStock(editionId, 5, "Not found"))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void record_damaged_should_decrement() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    var result = inventoryService.recordDamaged(editionId, 3, "Torn cover");

    assertThat(result.getQuantityOnHand()).isEqualTo(7);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_damaged_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    assertThatThrownBy(() -> inventoryService.recordDamaged(editionId, 20, "Too many damaged"))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void record_lost_should_decrement() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    var result = inventoryService.recordLost(editionId, 2, "Misplaced");

    assertThat(result.getQuantityOnHand()).isEqualTo(8);
    then(inventoryMovementRepository).should().save(any(InventoryMovement.class));
  }

  @Test
  void record_lost_when_insufficient_should_throw() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    assertThatThrownBy(() -> inventoryService.recordLost(editionId, 20, "Too many lost"))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void get_movements_by_edition_should_return_list() {
    given(inventoryMovementRepository.findByBookEditionId(editionId))
        .willReturn(java.util.List.of());

    var result = inventoryService.getMovementsByEdition(editionId);

    assertThat(result).isEmpty();
    then(inventoryMovementRepository).should().findByBookEditionId(editionId);
  }

  @Test
  void getBookStock_shouldReturnTotalStock() {
    given(bookRepository.existsById(bookId)).willReturn(true);
    given(inventoryItemRepository.sumQuantityByBookId(bookId)).willReturn(25);

    Integer result = inventoryService.getBookStock(bookId);

    assertThat(result).isEqualTo(25);
    then(bookRepository).should().existsById(bookId);
    then(inventoryItemRepository).should().sumQuantityByBookId(bookId);
  }

  @Test
  void getBookStock_whenBookNotFound_shouldThrow() {
    given(bookRepository.existsById(bookId)).willReturn(false);

    assertThatThrownBy(() -> inventoryService.getBookStock(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found");
  }

  @Test
  void getEditionStock_shouldReturnQuantity() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.of(item));

    Integer stock = inventoryService.getEditionStock(editionId);

    assertThat(stock).isEqualTo(10);
    then(inventoryItemRepository).should().findByBookEditionId(editionId);
  }

  @Test
  void getEditionStock_withNoInventory_shouldReturnZero() {
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(Optional.empty());

    Integer stock = inventoryService.getEditionStock(editionId);

    assertThat(stock).isEqualTo(0);
    then(inventoryItemRepository).should().findByBookEditionId(editionId);
  }
}
