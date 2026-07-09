package com.onlydevs.bookstore.model.dto.response;

import java.math.BigDecimal;
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
public class SaleItemResponse {

  private UUID id;
  private UUID editionId;
  private String isbn;
  private String bookTitle;
  private String format;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal lineTotal;
}
