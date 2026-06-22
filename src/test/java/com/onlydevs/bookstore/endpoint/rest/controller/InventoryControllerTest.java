package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.dto.request.AdjustStockRequest;
import com.onlydevs.bookstore.model.dto.request.ArrivalRequest;
import com.onlydevs.bookstore.model.dto.request.StockLossRequest;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.service.InventoryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private InventoryService inventoryService;

  @MockBean private InventoryMapper inventoryMapper;

  private final UUID storeId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();

  @Test
  void get_inventory_should_return_list() throws Exception {
    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(10)
            .reorderLevel(5)
            .lowStock(false)
            .build();

    given(inventoryService.getInventoryByStore(storeId)).willReturn(List.of());
    given(inventoryMapper.toRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/inventory", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].quantityOnHand").value(10));
  }

  @Test
  void get_stock_by_edition_should_return_item() throws Exception {
    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(10)
            .build();

    given(inventoryService.getStockByEdition(storeId, editionId)).willReturn(new InventoryItem());
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/inventory/{editionId}", storeId, editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantityOnHand").value(10));
  }

  @Test
  void record_arrival_should_return_created() throws Exception {
    ArrivalRequest request =
        ArrivalRequest.builder().editionId(editionId).quantity(5).reference("REF-001").build();

    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(15)
            .build();

    given(inventoryService.recordArrival(any(), any(), any(), any()))
        .willReturn(new InventoryItem());
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/inventory/arrival", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantityOnHand").value(15));
  }

  @Test
  void adjust_stock_should_return_created() throws Exception {
    AdjustStockRequest request =
        AdjustStockRequest.builder().editionId(editionId).quantity(5).reason("Add stock").build();

    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(15)
            .build();

    given(inventoryService.adjustStock(any(), any(), any(), any())).willReturn(new InventoryItem());
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/inventory/adjustment", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantityOnHand").value(15));
  }

  @Test
  void record_damaged_should_return_created() throws Exception {
    StockLossRequest request =
        StockLossRequest.builder().editionId(editionId).quantity(2).reason("Torn cover").build();

    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(8)
            .build();

    given(inventoryService.recordDamaged(any(), any(), any(), any()))
        .willReturn(new InventoryItem());
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/inventory/damaged", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantityOnHand").value(8));
  }

  @Test
  void record_lost_should_return_created() throws Exception {
    StockLossRequest request =
        StockLossRequest.builder().editionId(editionId).quantity(1).reason("Misplaced").build();

    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(9)
            .build();

    given(inventoryService.recordLost(any(), any(), any(), any())).willReturn(new InventoryItem());
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/inventory/lost", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantityOnHand").value(9));
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
        .perform(get("/api/v1/editions/{editionId}/movements", editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("ARRIVAL"));
  }

  @Test
  void get_movements_without_type_should_return_all() throws Exception {
    InventoryMovementResponse response =
        InventoryMovementResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .type("ARRIVAL")
            .build();

    given(inventoryService.getMovements(any(), anyString())).willReturn(List.of());
    given(inventoryMapper.toMovementRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/movements", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("ARRIVAL"));
  }

  @Test
  void get_movements_with_valid_type_should_filter() throws Exception {
    InventoryMovementResponse response =
        InventoryMovementResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .type("ARRIVAL")
            .build();

    given(inventoryService.getMovements(any(), anyString())).willReturn(List.of());
    given(inventoryMapper.toMovementRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/movements?type=ARRIVAL", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("ARRIVAL"));
  }

  @Test
  void get_movements_with_invalid_type_should_return_bad_request() throws Exception {
    given(inventoryService.getMovements(any(), anyString()))
        .willThrow(new BadRequestException("Unknown inventory movement type: INVALID"));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/movements?type=INVALID", storeId))
        .andExpect(status().isBadRequest());
  }
}
