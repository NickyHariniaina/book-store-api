package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.model.dto.request.CreateSaleItemRequest;
import com.onlydevs.bookstore.model.dto.request.CreateSaleRequest;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.model.enums.PaymentMethod;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.service.SaleService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc(addFilters = false)
class SaleControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private SaleService saleService;

  private final UUID saleId = UUID.randomUUID();

  @Test
  void createSale_ShouldReturnCreated() throws Exception {
    var itemRequest =
        CreateSaleItemRequest.builder()
            .editionId(UUID.randomUUID())
            .quantity(2)
            .unitPrice(new BigDecimal("10.00"))
            .build();
    var request = CreateSaleRequest.builder().customerId(null).items(List.of(itemRequest)).build();

    SaleResponse response =
        SaleResponse.builder()
            .id(saleId)
            .status(SaleStatus.PENDING)
            .total(new BigDecimal("20.00"))
            .items(List.of())
            .build();

    given(saleService.createSale(any(), any())).willReturn(response);

    mockMvc
        .perform(
            post("/sales")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(saleId.toString()))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void getAllSales_ShouldReturnPage() throws Exception {
    SaleResponse response =
        SaleResponse.builder()
            .id(saleId)
            .status(SaleStatus.PAID)
            .total(new BigDecimal("19.99"))
            .items(List.of())
            .build();

    Page<SaleResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
    given(saleService.getAllSales(any())).willReturn(page);

    mockMvc
        .perform(get("/sales"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].status").value("PAID"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void getSale_ShouldReturnSale() throws Exception {
    SaleResponse response = SaleResponse.builder().id(saleId).status(SaleStatus.PENDING).build();

    given(saleService.getSale(saleId)).willReturn(response);

    mockMvc
        .perform(get("/sales/{id}", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saleId.toString()));
  }

  @Test
  void confirmSale_ShouldReturnOk() throws Exception {
    SaleResponse response =
        SaleResponse.builder()
            .id(saleId)
            .status(SaleStatus.PAID)
            .paymentMethod(PaymentMethod.CARD)
            .total(new BigDecimal("19.99"))
            .build();

    given(saleService.confirmSale(any(), any())).willReturn(response);

    mockMvc
        .perform(patch("/sales/{id}/confirm", saleId).param("paymentMethod", "CARD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PAID"))
        .andExpect(jsonPath("$.paymentMethod").value("CARD"));
  }

  @Test
  void cancelSale_ShouldReturnOk() throws Exception {
    SaleResponse response = SaleResponse.builder().id(saleId).status(SaleStatus.CANCELLED).build();

    given(saleService.cancelSale(saleId)).willReturn(response);

    mockMvc
        .perform(patch("/sales/{id}/cancel", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void refundSale_ShouldReturnOk() throws Exception {
    SaleResponse response = SaleResponse.builder().id(saleId).status(SaleStatus.REFUNDED).build();

    given(saleService.refundSale(saleId)).willReturn(response);

    mockMvc
        .perform(patch("/sales/{id}/refund", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REFUNDED"));
  }
}
