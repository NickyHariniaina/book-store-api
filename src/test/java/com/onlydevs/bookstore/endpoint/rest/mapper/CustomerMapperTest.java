package com.onlydevs.bookstore.endpoint.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.onlydevs.bookstore.model.BookStore;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.SaleItem;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private CustomerMapper customerMapper;

  private Customer customer;
  private Sale sale;
  private SaleItem saleItem;

  @BeforeEach
  void setUp() {
    customerMapper = new CustomerMapper();

    customer =
        Customer.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("+123456789")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    BookStore bookStore = BookStore.builder().id(UUID.randomUUID()).name("Main Store").build();

    sale =
        Sale.builder()
            .id(UUID.randomUUID())
            .bookStore(bookStore)
            .customer(customer)
            .status(SaleStatus.COMPLETED)
            .paymentMethod(PaymentMethod.CREDIT_CARD)
            .createdAt(Instant.now())
            .build();

    saleItem =
        SaleItem.builder()
            .id(UUID.randomUUID())
            .sale(sale)
            .quantity(2)
            .unitPrice(new BigDecimal("10.00"))
            .discountPercent(BigDecimal.ZERO)
            .build();

    sale.setSaleItems(List.of(saleItem));
  }

  @Test
  void toDomain_ShouldMapCreateRequestToCustomer() {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder()
            .firstName("Jane")
            .lastName("Smith")
            .email("jane.smith@example.com")
            .phone("+987654321")
            .build();

    Customer result = customerMapper.toDomain(request);

    assertThat(result.getId()).isNull();
    assertThat(result.getFirstName()).isEqualTo("Jane");
    assertThat(result.getLastName()).isEqualTo("Smith");
    assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
    assertThat(result.getPhone()).isEqualTo("+987654321");
  }

  @Test
  void toDomain_WhenPhoneIsNull_ShouldMapWithNullPhone() {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder()
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .build();

    Customer result = customerMapper.toDomain(request);

    assertThat(result.getPhone()).isNull();
  }

  @Test
  void updateDomain_ShouldUpdateNonNullFields() {
    UpdateCustomerRequest request =
        UpdateCustomerRequest.builder().firstName("Jane").email("jane@example.com").build();

    customerMapper.updateDomain(customer, request);

    assertThat(customer.getFirstName()).isEqualTo("Jane");
    assertThat(customer.getLastName()).isEqualTo("Doe");
    assertThat(customer.getEmail()).isEqualTo("jane@example.com");
    assertThat(customer.getPhone()).isEqualTo("+123456789");
  }

  @Test
  void updateDomain_WhenAllFieldsNull_ShouldNotChange() {
    UpdateCustomerRequest request = UpdateCustomerRequest.builder().build();

    customerMapper.updateDomain(customer, request);

    assertThat(customer.getFirstName()).isEqualTo("John");
    assertThat(customer.getLastName()).isEqualTo("Doe");
    assertThat(customer.getEmail()).isEqualTo("john.doe@example.com");
    assertThat(customer.getPhone()).isEqualTo("+123456789");
  }

  @Test
  void toRest_ShouldMapCustomerToResponse() {
    CustomerResponse result = customerMapper.toRest(customer);

    assertThat(result.getId()).isEqualTo(customer.getId());
    assertThat(result.getFirstName()).isEqualTo("John");
    assertThat(result.getLastName()).isEqualTo("Doe");
    assertThat(result.getFullName()).isEqualTo("John Doe");
    assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    assertThat(result.getPhone()).isEqualTo("+123456789");
    assertThat(result.getCreatedAt()).isEqualTo(customer.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(customer.getUpdatedAt());
  }

  @Test
  void toRest_WhenCustomerIsNull_ShouldReturnNull() {
    assertThat(customerMapper.toRest((Customer) null)).isNull();
  }

  @Test
  void toRest_WhenPhoneIsNull_ShouldMapWithNullPhone() {
    customer.setPhone(null);

    CustomerResponse result = customerMapper.toRest(customer);

    assertThat(result.getPhone()).isNull();
  }

  @Test
  void toRest_List_ShouldMapListOfCustomers() {
    Customer customer2 =
        Customer.builder()
            .id(UUID.randomUUID())
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .build();

    List<CustomerResponse> result = customerMapper.toRest(List.of(customer, customer2));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getFullName()).isEqualTo("John Doe");
    assertThat(result.get(1).getFullName()).isEqualTo("Jane Smith");
  }

  @Test
  void toSaleSummary_ShouldMapSaleToResponse() {
    SaleSummaryResponse result = customerMapper.toSaleSummary(sale);

    assertThat(result.getId()).isEqualTo(sale.getId());
    assertThat(result.getStoreId()).isEqualTo(sale.getBookStore().getId());
    assertThat(result.getStoreName()).isEqualTo("Main Store");
    assertThat(result.getCustomerId()).isEqualTo(customer.getId());
    assertThat(result.getCustomerName()).isEqualTo("John Doe");
    assertThat(result.getStatus()).isEqualTo(SaleStatus.COMPLETED);
    assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
  }

  @Test
  void toSaleSummary_ShouldComputeTotal() {
    SaleSummaryResponse result = customerMapper.toSaleSummary(sale);

    assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
  }

  @Test
  void toSaleSummary_WhenSaleHasNoItems_ShouldReturnZeroTotal() {
    sale.setSaleItems(List.of());

    SaleSummaryResponse result = customerMapper.toSaleSummary(sale);

    assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void toSaleSummary_WhenSaleIsNull_ShouldReturnNull() {
    assertThat(customerMapper.toSaleSummary(null)).isNull();
  }

  @Test
  void toSaleSummary_WhenCustomerIsNull_ShouldMapWithNullCustomerFields() {
    sale.setCustomer(null);

    SaleSummaryResponse result = customerMapper.toSaleSummary(sale);

    assertThat(result.getCustomerId()).isNull();
    assertThat(result.getCustomerName()).isNull();
  }

  @Test
  void toSaleSummary_List_ShouldMapListOfSales() {
    Sale sale2 =
        Sale.builder()
            .id(UUID.randomUUID())
            .bookStore(sale.getBookStore())
            .status(SaleStatus.PENDING)
            .saleItems(List.of())
            .build();

    List<SaleSummaryResponse> result = customerMapper.toSaleSummary(List.of(sale, sale2));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getStatus()).isEqualTo(SaleStatus.COMPLETED);
    assertThat(result.get(1).getStatus()).isEqualTo(SaleStatus.PENDING);
  }

  @Test
  void toSaleSummary_WithDiscount_ShouldComputeDiscountedTotal() {
    saleItem.setDiscountPercent(new BigDecimal("10.00"));

    SaleSummaryResponse result = customerMapper.toSaleSummary(sale);

    assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("18.00"));
  }
}
