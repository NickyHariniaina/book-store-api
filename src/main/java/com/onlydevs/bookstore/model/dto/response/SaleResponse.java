package com.onlydevs.bookstore.model.dto.response;

import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
public class SaleResponse {

  private UUID id;
  private UUID storeId;
  private String storeName;
  private UUID customerId;
  private String customerName;
  private SaleStatus status;
  private PaymentMethod paymentMethod;
  private BigDecimal total;
  private List<SaleItemResponse> items;
  private Instant createdAt;
  private Instant updatedAt;
}
