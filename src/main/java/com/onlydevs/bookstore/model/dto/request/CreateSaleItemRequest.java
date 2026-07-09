package com.onlydevs.bookstore.model.dto.request;

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
public class CreateSaleItemRequest {
  private UUID editionId;
  private Integer quantity;
  private BigDecimal unitPrice;
}
