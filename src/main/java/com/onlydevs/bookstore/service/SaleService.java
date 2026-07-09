package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.SaleMapper;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.SaleItem;
import com.onlydevs.bookstore.model.dto.request.AddSaleItemRequest;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookPriceHistoryRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.CustomerRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import com.onlydevs.bookstore.repository.SaleItemRepository;
import com.onlydevs.bookstore.repository.SaleRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final SaleItemRepository saleItemRepository;
  private final BookStoreRepository bookStoreRepository;
  private final BookEditionRepository bookEditionRepository;
  private final BookPriceHistoryRepository bookPriceHistoryRepository;
  private final CustomerRepository customerRepository;
  private final InventoryItemRepository inventoryItemRepository;
  private final InventoryMovementRepository inventoryMovementRepository;
  private final SaleMapper saleMapper;

  @Transactional
  public SaleResponse createSale(UUID storeId, UUID customerId) {
    BookStore store =
        bookStoreRepository
            .findById(storeId)
            .orElseThrow(() -> new NotFoundException("Store not found with id: " + storeId));

    Customer customer = null;
    if (customerId != null) {
      customer =
          customerRepository
              .findById(customerId)
              .orElseThrow(
                  () -> new NotFoundException("Customer not found with id: " + customerId));
    }

    Sale sale =
        Sale.builder().bookStore(store).customer(customer).status(SaleStatus.PENDING).build();

    Sale saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse addItem(UUID saleId, AddSaleItemRequest request) {
    Sale sale = findPendingSale(saleId);

    BookEdition edition =
        bookEditionRepository
            .findById(request.getEditionId())
            .orElseThrow(
                () ->
                    new NotFoundException("Edition not found with id: " + request.getEditionId()));

    BigDecimal unitPrice = getCurrentPrice(edition.getId());

    SaleItem item =
        SaleItem.builder()
            .sale(sale)
            .bookEdition(edition)
            .quantity(request.getQuantity())
            .unitPrice(unitPrice)
            .discountPercent(request.getDiscountPercent())
            .build();

    sale.getSaleItems().add(item);
    saleItemRepository.save(item);

    return saleMapper.toRest(sale);
  }

  @Transactional
  public SaleResponse removeItem(UUID saleId, UUID itemId) {
    Sale sale = findPendingSale(saleId);

    boolean removed = sale.getSaleItems().removeIf(item -> item.getId().equals(itemId));
    if (!removed) {
      throw new NotFoundException("Item not found with id: " + itemId);
    }

    saleItemRepository.deleteByIdAndSaleId(itemId, saleId);
    return saleMapper.toRest(sale);
  }

  @Transactional
  public SaleResponse confirmSale(UUID saleId, PaymentMethod paymentMethod) {
    Sale sale = findPendingSale(saleId);

    if (paymentMethod == null) {
      throw new BadRequestException("Payment method is required to confirm a sale");
    }

    if (sale.getSaleItems() == null || sale.getSaleItems().isEmpty()) {
      throw new BadRequestException("Cannot confirm a sale with no items");
    }

    for (SaleItem item : sale.getSaleItems()) {
      decrementStock(
          sale.getBookStore().getId(), item.getBookEdition().getId(), item.getQuantity());
    }

    sale.setStatus(SaleStatus.PAID);
    sale.setPaymentMethod(paymentMethod);
    Sale saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse cancelSale(UUID saleId) {
    Sale sale = findPendingSale(saleId);
    sale.setStatus(SaleStatus.CANCELLED);
    Sale saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse refundSale(UUID saleId) {
    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale not found with id: " + saleId));

    if (sale.getStatus() != SaleStatus.PAID) {
      throw new BadRequestException("Only PAID sales can be refunded");
    }

    for (SaleItem item : sale.getSaleItems()) {
      reincrementStock(
          sale.getBookStore().getId(), item.getBookEdition().getId(), item.getQuantity());
    }

    sale.setStatus(SaleStatus.REFUNDED);
    Sale saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  public SaleResponse getSale(UUID saleId) {
    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale not found with id: " + saleId));
    return saleMapper.toRest(sale);
  }

  public Page<SaleResponse> getStoreSales(UUID storeId, Pageable pageable) {
    if (!bookStoreRepository.existsById(storeId)) {
      throw new NotFoundException("Store not found with id: " + storeId);
    }
    return saleRepository.findByBookStoreId(storeId, pageable).map(saleMapper::toRest);
  }

  private Sale findPendingSale(UUID saleId) {
    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale not found with id: " + saleId));
    if (sale.getStatus() != SaleStatus.PENDING) {
      throw new BadRequestException(
          "Sale is not in PENDING status (current: " + sale.getStatus() + ")");
    }
    return sale;
  }

  private void decrementStock(UUID storeId, UUID editionId, Integer quantity) {
    InventoryItem item =
        inventoryItemRepository
            .findByBookStoreIdAndBookEditionId(storeId, editionId)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        "No stock found for edition " + editionId + " at store " + storeId));

    int newQuantity = item.getQuantityOnHand() - quantity;
    if (newQuantity < 0) {
      throw new BadRequestException(
          "Insufficient stock for edition "
              + editionId
              + " at store "
              + storeId
              + ": available="
              + item.getQuantityOnHand()
              + ", requested="
              + quantity);
    }

    item.setQuantityOnHand(newQuantity);
    inventoryItemRepository.save(item);

    createMovement(storeId, editionId, InventoryMovementType.SALE, quantity, "Sale confirmation");
  }

  private void reincrementStock(UUID storeId, UUID editionId, Integer quantity) {
    InventoryItem item =
        inventoryItemRepository
            .findByBookStoreIdAndBookEditionId(storeId, editionId)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        "No stock found for edition " + editionId + " at store " + storeId));

    item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
    inventoryItemRepository.save(item);

    createMovement(storeId, editionId, InventoryMovementType.RETURN, quantity, "Sale refund");
  }

  private void createMovement(
      UUID storeId, UUID editionId, InventoryMovementType type, Integer quantity, String reason) {
    BookStore store = bookStoreRepository.getReferenceById(storeId);
    BookEdition edition = bookEditionRepository.getReferenceById(editionId);

    InventoryMovement movement =
        InventoryMovement.builder()
            .bookStore(store)
            .bookEdition(edition)
            .inventoryMovementType(type)
            .quantity(quantity)
            .reason(reason)
            .reference("SALE-" + UUID.randomUUID())
            .build();

    inventoryMovementRepository.save(movement);
  }

  private BigDecimal getCurrentPrice(UUID editionId) {
    return bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId)
        .map(price -> price.getPrice())
        .orElse(BigDecimal.ZERO);
  }
}
