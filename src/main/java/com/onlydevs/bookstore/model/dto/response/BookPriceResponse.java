package com.onlydevs.bookstore.model.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPriceResponse {
  private UUID id;
  private UUID editionId;
  private BigDecimal price;
  private Instant effectiveFrom;
  private Instant effectiveTo;
  private Instant createdAt;
}
