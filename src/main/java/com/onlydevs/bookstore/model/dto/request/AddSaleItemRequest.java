package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class AddSaleItemRequest {

  @NotNull private UUID editionId;

  @NotNull @Positive private Integer quantity;

  @DecimalMin("0.00")
  private BigDecimal discountPercent;
}
