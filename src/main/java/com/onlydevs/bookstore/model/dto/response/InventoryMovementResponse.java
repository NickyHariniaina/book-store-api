package com.onlydevs.bookstore.model.dto.response;

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
public class InventoryMovementResponse {

  private UUID id;
  private UUID storeId;
  private UUID editionId;
  private String bookTitle;
  private String isbn;
  private String type;
  private Integer quantity;
  private Integer signedQuantity;
  private String reason;
  private String reference;
  private Instant movedAt;
  private Instant createdAt;
}
