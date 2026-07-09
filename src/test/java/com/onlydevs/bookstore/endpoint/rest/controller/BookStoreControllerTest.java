package com.onlydevs.bookstore.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydevs.bookstore.endpoint.rest.mapper.InventoryMapper;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.dto.request.AdjustStockRequest;
import com.onlydevs.bookstore.model.dto.request.ArrivalRequest;
import com.onlydevs.bookstore.model.dto.request.CreateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.request.StockLossRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookStoreRequest;
import com.onlydevs.bookstore.model.dto.response.BookStoreResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryItemResponse;
import com.onlydevs.bookstore.model.dto.response.InventoryMovementResponse;
import com.onlydevs.bookstore.service.BookStoreService;
import com.onlydevs.bookstore.service.InventoryService;
import java.time.Instant;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookStoreController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookStoreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookStoreService bookStoreService;

  @MockBean private InventoryService inventoryService;

  @MockBean private InventoryMapper inventoryMapper;

  private final UUID storeId = UUID.randomUUID();
  private final UUID editionId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  @Test
  void get_all_stores_should_return_page_of_stores() throws Exception {
    BookStoreResponse response =
        BookStoreResponse.builder()
            .id(storeId)
            .name("Test Store")
            .address("123 Test St")
            .phone("0340000000")
            .email("test@store.com")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Page<BookStoreResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

    given(bookStoreService.getAllStores(any())).willReturn(page);

    mockMvc
        .perform(get("/stores").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(storeId.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Test Store"))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void get_store_by_id_should_return_store() throws Exception {
    BookStoreResponse response = BookStoreResponse.builder().id(storeId).name("Test Store").build();

    given(bookStoreService.getStoreById(storeId)).willReturn(response);

    mockMvc
        .perform(get("/stores/{id}", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(storeId.toString()))
        .andExpect(jsonPath("$.name").value("Test Store"));
  }

  @Test
  void create_store_should_return_created() throws Exception {
    CreateBookStoreRequest request =
        CreateBookStoreRequest.builder()
            .name("New Store")
            .address("456 New St")
            .phone("0340000001")
            .email("new@store.com")
            .build();

    BookStoreResponse response = BookStoreResponse.builder().id(storeId).name("New Store").build();

    given(bookStoreService.createStore(any())).willReturn(response);

    mockMvc
        .perform(
            post("/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Store"));
  }

  @Test
  void create_store_with_invalid_body_should_return_bad_request() throws Exception {
    CreateBookStoreRequest request = CreateBookStoreRequest.builder().build();

    mockMvc
        .perform(
            post("/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_store_should_return_ok() throws Exception {
    UpdateBookStoreRequest request = UpdateBookStoreRequest.builder().name("Updated Store").build();

    BookStoreResponse response =
        BookStoreResponse.builder().id(storeId).name("Updated Store").build();

    given(bookStoreService.updateStore(any(), any())).willReturn(response);

    mockMvc
        .perform(
            put("/stores/{id}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Store"));
  }

  @Test
  void delete_store_should_return_no_content() throws Exception {
    willDoNothing().given(bookStoreService).deleteStore(storeId);

    mockMvc.perform(delete("/stores/{id}", storeId)).andExpect(status().isNoContent());
  }

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

    given(bookStoreService.getInventoryByStore(storeId)).willReturn(List.of());
    given(inventoryMapper.toRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/stores/{id}/inventory", storeId))
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

    given(bookStoreService.getStockByEdition(storeId, editionId)).willReturn(null);
    given(inventoryMapper.toRest(any())).willReturn(response);

    mockMvc
        .perform(get("/stores/{id}/inventory/{editionId}", storeId, editionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantityOnHand").value(10));
  }

  @Test
  void get_book_stock_by_store_should_return_stock() throws Exception {
    given(bookStoreService.getBookStockByStore(storeId, bookId)).willReturn(50);

    mockMvc
        .perform(get("/stores/{id}/books/{bookId}/stock", storeId, bookId))
        .andExpect(status().isOk())
        .andExpect(content().string("50"));
  }

  @Test
  void get_low_stock_should_return_list() throws Exception {
    InventoryItemResponse response =
        InventoryItemResponse.builder()
            .id(UUID.randomUUID())
            .storeId(storeId)
            .editionId(editionId)
            .quantityOnHand(2)
            .reorderLevel(5)
            .lowStock(true)
            .build();

    given(bookStoreService.getLowStockItems(storeId)).willReturn(List.of());
    given(inventoryMapper.toRestList(any())).willReturn(List.of(response));

    mockMvc
        .perform(get("/stores/{id}/low-stock", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].lowStock").value(true));
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
            post("/stores/{storeId}/inventory/arrival", storeId)
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
            post("/stores/{storeId}/inventory/adjustment", storeId)
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
            post("/stores/{storeId}/inventory/damaged", storeId)
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
            post("/stores/{storeId}/inventory/lost", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantityOnHand").value(9));
  }

  @Test
  void get_movements_should_return_list() throws Exception {
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
        .perform(get("/stores/{storeId}/movements", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("ARRIVAL"));
  }
}
