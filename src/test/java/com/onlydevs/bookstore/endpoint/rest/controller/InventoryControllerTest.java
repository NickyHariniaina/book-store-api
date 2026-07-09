package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.InventoryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private InventoryService inventoryService;

  @MockBean private InventoryMapper inventoryMapper;

  private final UUID storeId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();

  @Test
  void get_edition_stock_should_return_stock() throws Exception {
    given(inventoryService.getEditionStock(editionId)).willReturn(42);

    mockMvc
        .perform(get("/editions/{editionId}/stock", editionId))
        .andExpect(status().isOk())
        .andExpect(content().string("42"));
  }

  @Test
  void get_movements_by_edition_should_return_list() throws Exception {
    InventoryMovementResponse response =
        InventoryMovementResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .type("ARRIVAL")
            .quantity(5)
            .signedQuantity(5)
            .build();

    given(inventoryService.getMovementsByEdition(editionId)).willReturn(List.of());
    given(inventoryMapper.toMovementRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/editions/{editionId}/movements", editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("ARRIVAL"));
  }
}
