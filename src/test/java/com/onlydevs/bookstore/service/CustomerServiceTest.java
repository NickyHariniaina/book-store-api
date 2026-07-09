package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.mapper.CustomerMapper;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.CustomerRepository;
import com.onlydevs.bookstore.repository.SaleRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;
  @Mock private SaleRepository saleRepository;
  @Mock private CustomerMapper customerMapper;

  @InjectMocks private CustomerService customerService;

  private UUID customerId;
  private Customer customer;
  private CustomerResponse customerResponse;
  private CreateCustomerRequest createRequest;
  private UpdateCustomerRequest updateRequest;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();

    customer =
        Customer.builder()
            .id(customerId)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("+123456789")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    customerResponse =
        CustomerResponse.builder()
            .id(customerId)
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .email("john.doe@example.com")
            .phone("+123456789")
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt())
            .build();

    createRequest =
        CreateCustomerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("+123456789")
            .build();

    updateRequest = UpdateCustomerRequest.builder().firstName("Jane").build();
  }

  @Test
  void getAllCustomers_ShouldReturnListOfCustomers() {
    given(customerRepository.findAll()).willReturn(List.of(customer));
    given(customerMapper.toRest(List.of(customer))).willReturn(List.of(customerResponse));

    List<CustomerResponse> result = customerService.getAllCustomers();

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getFullName()).isEqualTo("John Doe");
    then(customerRepository).should().findAll();
  }

  @Test
  void getAllCustomers_WhenNoCustomers_ShouldReturnEmptyList() {
    given(customerRepository.findAll()).willReturn(List.of());
    given(customerMapper.toRest(List.of())).willReturn(List.of());

    List<CustomerResponse> result = customerService.getAllCustomers();

    assertThat(result).isEmpty();
    then(customerRepository).should().findAll();
  }

  @Test
  void getCustomerById_WhenCustomerExists_ShouldReturnResponse() {
    given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
    given(customerMapper.toRest(customer)).willReturn(customerResponse);

    CustomerResponse result = customerService.getCustomerById(customerId);

    assertThat(result).isNotNull();
    assertThat(result.getFullName()).isEqualTo("John Doe");
    then(customerRepository).should().findById(customerId);
  }

  @Test
  void getCustomerById_WhenCustomerNotFound_ShouldThrow() {
    given(customerRepository.findById(customerId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.getCustomerById(customerId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Customer not found");

    then(customerRepository).should().findById(customerId);
  }

  @Test
  void createCustomer_ShouldReturnCreatedResponse() {
    given(customerMapper.toDomain(createRequest)).willReturn(customer);
    given(customerRepository.save(customer)).willReturn(customer);
    given(customerMapper.toRest(customer)).willReturn(customerResponse);

    CustomerResponse result = customerService.createCustomer(createRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFullName()).isEqualTo("John Doe");
    then(customerRepository).should().save(customer);
  }

  @Test
  void updateCustomer_WhenCustomerExists_ShouldUpdateAndReturn() {
    given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
    given(customerRepository.save(customer)).willReturn(customer);
    given(customerMapper.toRest(customer)).willReturn(customerResponse);

    CustomerResponse result = customerService.updateCustomer(customerId, updateRequest);

    assertThat(result).isNotNull();
    then(customerRepository).should().findById(customerId);
    then(customerMapper).should().updateDomain(customer, updateRequest);
    then(customerRepository).should().save(customer);
  }

  @Test
  void updateCustomer_WhenCustomerNotFound_ShouldThrow() {
    given(customerRepository.findById(customerId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.updateCustomer(customerId, updateRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Customer not found");

    then(customerRepository).should().findById(customerId);
    then(customerRepository).should(never()).save(any());
  }

  @Test
  void getCustomerSales_WhenCustomerExists_ShouldReturnSales() {
    var sale = Sale.builder().id(UUID.randomUUID()).status(SaleStatus.PAID).build();

    SaleSummaryResponse saleResponse =
        SaleSummaryResponse.builder().id(sale.getId()).status(SaleStatus.PAID).build();

    given(customerRepository.existsById(customerId)).willReturn(true);
    given(saleRepository.findByCustomerIdOrderByCreatedAtDesc(customerId))
        .willReturn(List.of(sale));
    given(customerMapper.toSaleSummary(List.of(sale))).willReturn(List.of(saleResponse));

    List<SaleSummaryResponse> result = customerService.getCustomerSales(customerId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getStatus()).isEqualTo(SaleStatus.PAID);
    then(customerRepository).should().existsById(customerId);
    then(saleRepository).should().findByCustomerIdOrderByCreatedAtDesc(customerId);
  }

  @Test
  void getCustomerSales_WhenCustomerNotFound_ShouldThrow() {
    given(customerRepository.existsById(customerId)).willReturn(false);

    assertThatThrownBy(() -> customerService.getCustomerSales(customerId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Customer not found");

    then(customerRepository).should().existsById(customerId);
    then(saleRepository).should(never()).findByCustomerIdOrderByCreatedAtDesc(any());
  }

  @Test
  void getCustomerSales_WhenNoSales_ShouldReturnEmptyList() {
    given(customerRepository.existsById(customerId)).willReturn(true);
    given(saleRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).willReturn(List.of());
    given(customerMapper.toSaleSummary(List.of())).willReturn(List.of());

    List<SaleSummaryResponse> result = customerService.getCustomerSales(customerId);

    assertThat(result).isEmpty();
    then(customerRepository).should().existsById(customerId);
    then(saleRepository).should().findByCustomerIdOrderByCreatedAtDesc(customerId);
  }
}
