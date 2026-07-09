package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookPriceHistoryMapper;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookPriceHistory;
import com.onlydevs.bookstore.model.dto.request.CreateBookPriceRequest;
import com.onlydevs.bookstore.model.dto.response.BookPriceResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookPriceHistoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BookPriceHistoryService {
  private final BookPriceHistoryRepository bookPriceHistoryRepository;
  private final BookEditionRepository bookEditionRepository;
  private final BookPriceHistoryMapper bookPriceHistoryMapper;

  public List<BookPriceResponse> getPrices(UUID editionId) {
    if (!bookEditionRepository.existsById(editionId)) {
      throw new NotFoundException("Edition not found with id: " + editionId);
    }
    return bookPriceHistoryMapper.toResponseList(
        bookPriceHistoryRepository.findByBookEditionIdOrderByEffectiveFromDesc(editionId));
  }

  public BookPriceResponse getCurrentPrice(UUID editionId) {
    if (!bookEditionRepository.existsById(editionId)) {
      throw new NotFoundException("Edition not found with id: " + editionId);
    }
    BookPriceHistory price =
        bookPriceHistoryRepository
            .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId)
            .orElseThrow(() -> new NotFoundException("No price found for edition id: " + editionId));
    return bookPriceHistoryMapper.toResponse(price);
  }

  @Transactional
  public BookPriceResponse createPrice(UUID editionId, CreateBookPriceRequest request) {
    BookEdition edition =
        bookEditionRepository
            .findById(editionId)
            .orElseThrow(() -> new NotFoundException("Edition not found with id: " + editionId));

    bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId)
        .ifPresent(
            previous -> {
              if (previous.getEffectiveTo() == null) {
                previous.setEffectiveTo(request.getEffectiveFrom());
                bookPriceHistoryRepository.save(previous);
              }
            });

    BookPriceHistory newPrice =
        BookPriceHistory.builder()
            .price(request.getPrice())
            .effectiveFrom(request.getEffectiveFrom())
            .bookEdition(edition)
            .build();

    BookPriceHistory saved = bookPriceHistoryRepository.save(newPrice);
    return bookPriceHistoryMapper.toResponse(saved);
  }
}
