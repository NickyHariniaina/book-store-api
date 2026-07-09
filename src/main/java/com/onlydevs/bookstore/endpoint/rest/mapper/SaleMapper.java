package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.SaleItem;
import com.onlydevs.bookstore.model.dto.response.SaleItemResponse;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public SaleResponse toRest(Sale sale) {
    if (sale == null) {
      return null;
    }
    return SaleResponse.builder()
        .id(sale.getId())
        .storeId(sale.getBookStore().getId())
        .storeName(sale.getBookStore().getName())
        .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
        .customerName(
            sale.getCustomer() != null
                ? sale.getCustomer().getFirstName() + " " + sale.getCustomer().getLastName()
                : null)
        .status(sale.getStatus())
        .paymentMethod(sale.getPaymentMethod())
        .total(computeTotal(sale))
        .items(toItemRestList(sale.getSaleItems()))
        .createdAt(sale.getCreatedAt())
        .updatedAt(sale.getUpdatedAt())
        .build();
  }

  public List<SaleResponse> toRestList(List<Sale> sales) {
    return sales.stream().map(this::toRest).collect(Collectors.toList());
  }

  public SaleItemResponse toItemRest(SaleItem item) {
    if (item == null) {
      return null;
    }
    BookEdition edition = item.getBookEdition();
    BigDecimal unitPrice = item.getUnitPrice();
    var quantity = BigDecimal.valueOf(item.getQuantity());
    BigDecimal lineTotal = unitPrice.multiply(quantity);

    if (item.getDiscountPercent() != null
        && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal discount =
          lineTotal
              .multiply(item.getDiscountPercent())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      lineTotal = lineTotal.subtract(discount);
    }

    return SaleItemResponse.builder()
        .id(item.getId())
        .editionId(edition.getId())
        .isbn(edition.getIsbn())
        .bookTitle(edition.getBook().getTitle())
        .format(String.valueOf(edition.getFormat()))
        .quantity(item.getQuantity())
        .unitPrice(unitPrice)
        .discountPercent(item.getDiscountPercent())
        .lineTotal(lineTotal)
        .build();
  }

  public List<SaleItemResponse> toItemRestList(List<SaleItem> items) {
    return items.stream().map(this::toItemRest).collect(Collectors.toList());
  }

  private BigDecimal computeTotal(Sale sale) {
    if (sale.getSaleItems() == null || sale.getSaleItems().isEmpty()) {
      return BigDecimal.ZERO;
    }
    return sale.getSaleItems().stream()
        .map(
            item -> {
              var quantity = BigDecimal.valueOf(item.getQuantity());
              BigDecimal lineTotal = item.getUnitPrice().multiply(quantity);
              if (item.getDiscountPercent() != null
                  && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount =
                    lineTotal
                        .multiply(item.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                lineTotal = lineTotal.subtract(discount);
              }
              return lineTotal;
            })
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
