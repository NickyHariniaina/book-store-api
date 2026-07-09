package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.service.CustomerService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CustomerService customerService;

  private final UUID customerId = UUID.randomUUID();

  @Test
  void getAllCustomers_ShouldReturnList() throws Exception {
    var response =
        CustomerResponse.builder()
            .id(customerId)
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .email("john@example.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    given(customerService.getAllCustomers()).willReturn(List.of(response));

    mockMvc
        .perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(customerId.toString()))
        .andExpect(jsonPath("$[0].firstName").value("John"))
        .andExpect(jsonPath("$[0].lastName").value("Doe"))
        .andExpect(jsonPath("$[0].fullName").value("John Doe"))
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void getAllCustomers_WhenEmpty_ShouldReturnEmptyList() throws Exception {
    given(customerService.getAllCustomers()).willReturn(List.of());

    mockMvc
        .perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void getCustomerById_ShouldReturnCustomer() throws Exception {
    var response =
        CustomerResponse.builder()
            .id(customerId)
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .email("john@example.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    given(customerService.getCustomerById(customerId)).willReturn(response);

    mockMvc
        .perform(get("/customers/{id}", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.fullName").value("John Doe"));
  }

  @Test
  void createCustomer_ShouldReturnCreated() throws Exception {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();

    var response =
        CustomerResponse.builder()
            .id(customerId)
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .email("john@example.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    given(customerService.createCustomer(any())).willReturn(response);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fullName").value("John Doe"));
  }

  @Test
  void createCustomer_WithMissingFirstName_ShouldReturnBadRequest() throws Exception {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder().lastName("Doe").email("john@example.com").build();

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createCustomer_WithMissingLastName_ShouldReturnBadRequest() throws Exception {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder().firstName("John").email("john@example.com").build();

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createCustomer_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder().firstName("John").lastName("Doe").build();

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createCustomer_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
    CreateCustomerRequest request =
        CreateCustomerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("not-an-email")
            .build();

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateCustomer_ShouldReturnOk() throws Exception {
    var request = UpdateCustomerRequest.builder().firstName("Jane").build();

    var response =
        CustomerResponse.builder()
            .id(customerId)
            .firstName("Jane")
            .lastName("Doe")
            .fullName("Jane Doe")
            .email("john@example.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    given(customerService.updateCustomer(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.fullName").value("Jane Doe"));
  }

  @Test
  void getCustomerSales_ShouldReturnList() throws Exception {
    var saleResponse =
        SaleSummaryResponse.builder()
            .id(UUID.randomUUID())
            .customerId(customerId)
            .customerName("John Doe")
            .status(SaleStatus.PAID)
            .total(new BigDecimal("29.99"))
            .build();

    given(customerService.getCustomerSales(customerId)).willReturn(List.of(saleResponse));

    mockMvc
        .perform(get("/customers/{id}/sales", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("PAID"))
        .andExpect(jsonPath("$[0].total").value(29.99));
  }

  @Test
  void getCustomerSales_WhenNoSales_ShouldReturnEmptyList() throws Exception {
    given(customerService.getCustomerSales(customerId)).willReturn(List.of());

    mockMvc
        .perform(get("/customers/{id}/sales", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}
