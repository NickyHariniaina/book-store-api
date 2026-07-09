package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  private final UUID storeId = UUID.randomUUID();
  private final UUID saleId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();

  @Test
  void createSale_ShouldReturnCreated() throws Exception {
    SaleResponse response =
        SaleResponse.builder()
            .id(saleId)
            .storeId(storeId)
            .storeName("Main Store")
            .status(SaleStatus.PENDING)
            .total(BigDecimal.ZERO)
            .items(List.of())
            .build();

    given(saleService.createSale(any(), any())).willReturn(response);

    mockMvc
        .perform(post("/stores/{storeId}/sales", storeId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(saleId.toString()))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void getStoreSales_ShouldReturnPage() throws Exception {
    SaleResponse response =
        SaleResponse.builder()
            .id(saleId)
            .storeId(storeId)
            .status(SaleStatus.PAID)
            .total(new BigDecimal("19.99"))
            .items(List.of())
            .build();

    Page<SaleResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
    given(saleService.getStoreSales(any(), any())).willReturn(page);

    mockMvc
        .perform(get("/stores/{storeId}/sales", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].status").value("PAID"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void getSale_ShouldReturnSale() throws Exception {
    SaleResponse response =
        SaleResponse.builder().id(saleId).storeId(storeId).status(SaleStatus.PENDING).build();

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
            .storeId(storeId)
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
    SaleResponse response =
        SaleResponse.builder().id(saleId).storeId(storeId).status(SaleStatus.CANCELLED).build();

    given(saleService.cancelSale(saleId)).willReturn(response);

    mockMvc
        .perform(patch("/sales/{id}/cancel", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void refundSale_ShouldReturnOk() throws Exception {
    SaleResponse response =
        SaleResponse.builder().id(saleId).storeId(storeId).status(SaleStatus.REFUNDED).build();

    given(saleService.refundSale(saleId)).willReturn(response);

    mockMvc
        .perform(patch("/sales/{id}/refund", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REFUNDED"));
  }
}
