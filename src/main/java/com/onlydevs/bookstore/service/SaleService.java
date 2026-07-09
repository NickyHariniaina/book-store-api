package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.SaleMapper;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.CustomerRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import com.onlydevs.bookstore.repository.SaleRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final BookStoreRepository bookStoreRepository;
  private final BookEditionRepository bookEditionRepository;
  private final CustomerRepository customerRepository;
  private final InventoryItemRepository inventoryItemRepository;
  private final InventoryMovementRepository inventoryMovementRepository;
  private final SaleMapper saleMapper;

  @Transactional
  public SaleResponse createSale(UUID storeId, UUID customerId) {
    var store =
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

    var sale =
        Sale.builder().bookStore(store).customer(customer).status(SaleStatus.PENDING).build();

    var saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse confirmSale(UUID saleId, PaymentMethod paymentMethod) {
    var sale = findPendingSale(saleId);

    if (paymentMethod == null) {
      throw new BadRequestException("Payment method is required to confirm a sale");
    }

    if (sale.getSaleItems() == null || sale.getSaleItems().isEmpty()) {
      throw new BadRequestException("Cannot confirm a sale with no items");
    }

    for (var item : sale.getSaleItems()) {
      decrementStock(
          sale.getBookStore().getId(), item.getBookEdition().getId(), item.getQuantity());
    }

    sale.setStatus(SaleStatus.PAID);
    sale.setPaymentMethod(paymentMethod);
    var saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse cancelSale(UUID saleId) {
    var sale = findPendingSale(saleId);
    sale.setStatus(SaleStatus.CANCELLED);
    var saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  @Transactional
  public SaleResponse refundSale(UUID saleId) {
    var sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale not found with id: " + saleId));

    if (sale.getStatus() != SaleStatus.PAID) {
      throw new BadRequestException("Only PAID sales can be refunded");
    }

    for (var item : sale.getSaleItems()) {
      reincrementStock(
          sale.getBookStore().getId(), item.getBookEdition().getId(), item.getQuantity());
    }

    sale.setStatus(SaleStatus.REFUNDED);
    var saved = saleRepository.save(sale);
    return saleMapper.toRest(saved);
  }

  public SaleResponse getSale(UUID saleId) {
    var sale =
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
    var sale =
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
    var item =
        inventoryItemRepository
            .findByBookStoreIdAndBookEditionId(storeId, editionId)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        "No stock found for edition " + editionId + " at store " + storeId));

    var newQuantity = item.getQuantityOnHand() - quantity;
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
    var item =
        inventoryItemRepository
            .findByBookStoreIdAndBookEditionId(storeId, editionId)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        "No stock found for edition " + editionId + " at store " + storeId));

    item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
    inventoryItemRepository.save(item);

    createMovement(storeId, editionId, InventoryMovementType.ADJUSTMENT, quantity, "Sale refund");
  }

  private void createMovement(
      UUID storeId, UUID editionId, InventoryMovementType type, Integer quantity, String reason) {
    var store = bookStoreRepository.getReferenceById(storeId);
    var edition = bookEditionRepository.getReferenceById(editionId);

    var movement =
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
}
