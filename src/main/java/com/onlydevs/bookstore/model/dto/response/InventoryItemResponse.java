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
public class InventoryItemResponse {

  private UUID id;
  private UUID storeId;
  private String storeName;
  private UUID editionId;
  private String bookTitle;
  private String isbn;
  private Integer quantityOnHand;
  private Integer reorderLevel;
  private Boolean lowStock;
  private Instant updatedAt;
}
