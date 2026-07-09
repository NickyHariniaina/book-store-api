package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.BookPriceHistory;
import com.onlydevs.bookstore.model.dto.response.BookPriceResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookPriceHistoryMapper {
  public BookPriceResponse toResponse(BookPriceHistory price) {
    if (price == null) {
      return null;
    }
    return BookPriceResponse.builder()
        .id(price.getId())
        .editionId(price.getBookEdition().getId())
        .price(price.getPrice())
        .effectiveFrom(price.getEffectiveFrom())
        .effectiveTo(price.getEffectiveTo())
        .createdAt(price.getCreatedAt())
        .build();
  }

  public List<BookPriceResponse> toResponseList(List<BookPriceHistory> prices) {
    return prices.stream().map(this::toResponse).toList();
  }
}
