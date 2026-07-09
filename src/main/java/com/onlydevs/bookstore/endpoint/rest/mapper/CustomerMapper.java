package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

  public Customer toDomain(CreateCustomerRequest request) {
    return Customer.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .build();
  }

  public void updateDomain(Customer customer, UpdateCustomerRequest request) {
    if (request.getFirstName() != null) {
      customer.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      customer.setLastName(request.getLastName());
    }
    if (request.getEmail() != null) {
      customer.setEmail(request.getEmail());
    }
    if (request.getPhone() != null) {
      customer.setPhone(request.getPhone());
    }
  }

  public CustomerResponse toRest(Customer customer) {
    if (customer == null) {
      return null;
    }
    return CustomerResponse.builder()
        .id(customer.getId())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .fullName(customer.getFirstName() + " " + customer.getLastName())
        .email(customer.getEmail())
        .phone(customer.getPhone())
        .createdAt(customer.getCreatedAt())
        .updatedAt(customer.getUpdatedAt())
        .build();
  }

  public List<CustomerResponse> toRest(List<Customer> customers) {
    return customers.stream().map(this::toRest).collect(Collectors.toList());
  }

  public SaleSummaryResponse toSaleSummary(Sale sale) {
    if (sale == null) {
      return null;
    }
    return SaleSummaryResponse.builder()
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
        .build();
  }

  public List<SaleSummaryResponse> toSaleSummary(List<Sale> sales) {
    return sales.stream().map(this::toSaleSummary).collect(Collectors.toList());
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
