package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AdjustStockRequest {

  @NotNull private UUID editionId;

  @NotNull private Integer quantity;

  @Size(max = 500)
  private String reason;
}
