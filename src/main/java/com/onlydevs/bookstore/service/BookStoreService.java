package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookStoreMapper;
import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookStoreRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookStoreService {

  private final BookStoreRepository bookStoreRepository;
  private final BookStoreMapper bookStoreMapper;
  private final InventoryItemRepository inventoryItemRepository;

  public Page<BookStoreResponse> getAllStores(Pageable pageable) {
    return bookStoreRepository.findAll(pageable).map(bookStoreMapper::toRest);
  }

  public BookStoreResponse getStoreById(UUID id) {
    var bookStore =
        bookStoreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BookStore not found with id: " + id));
    return bookStoreMapper.toRest(bookStore);
  }

  @Transactional
  public BookStoreResponse createStore(CreateBookStoreRequest request) {
    var bookStore = bookStoreMapper.toDomain(request);
    var saved = bookStoreRepository.save(bookStore);
    return bookStoreMapper.toRest(saved);
  }

  @Transactional
  public BookStoreResponse updateStore(UUID id, UpdateBookStoreRequest request) {
    var bookStore =
        bookStoreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BookStore not found with id: " + id));

    if (request.getName() != null) {
      bookStore.setName(request.getName());
    }
    if (request.getAddress() != null) {
      bookStore.setAddress(request.getAddress());
    }
    if (request.getPhone() != null) {
      bookStore.setPhone(request.getPhone());
    }
    if (request.getEmail() != null) {
      bookStore.setEmail(request.getEmail());
    }

    var saved = bookStoreRepository.save(bookStore);
    return bookStoreMapper.toRest(saved);
  }

  @Transactional
  public void deleteStore(UUID id) {
    if (!bookStoreRepository.existsById(id)) {
      throw new NotFoundException("BookStore not found with id: " + id);
    }
    bookStoreRepository.deleteById(id);
  }

  public Integer getStockByEdition(UUID storeId, UUID editionId) {
    return inventoryItemRepository
        .findByBookStoreIdAndBookEditionId(storeId, editionId)
        .map(InventoryItem::getQuantityOnHand)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Stock not found for store " + storeId + " and edition " + editionId));
  }

  public Integer getBookStockByStore(UUID storeId, UUID bookId) {
    return inventoryItemRepository.sumQuantityByStoreIdAndBookId(storeId, bookId);
  }

  public List<InventoryItem> getLowStockItems(UUID storeId) {
    if (!bookStoreRepository.existsById(storeId)) {
      throw new NotFoundException("BookStore not found with id: " + storeId);
    }
    return inventoryItemRepository.findLowStockByStoreId(storeId);
  }
}
