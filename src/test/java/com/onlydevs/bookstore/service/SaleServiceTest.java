package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.onlydevs.bookstore.endpoint.rest.mapper.SaleMapper;
import com.onlydevs.bookstore.model.Sale;
import com.onlydevs.bookstore.model.dto.response.SaleResponse;
import com.onlydevs.bookstore.model.enums.SaleStatus;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookPriceHistoryRepository;
import com.onlydevs.bookstore.repository.CustomerRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import com.onlydevs.bookstore.repository.InventoryMovementRepository;
import com.onlydevs.bookstore.repository.SaleItemRepository;
import com.onlydevs.bookstore.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private SaleItemRepository saleItemRepository;
  @Mock private BookEditionRepository bookEditionRepository;
  @Mock private BookPriceHistoryRepository bookPriceHistoryRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private InventoryItemRepository inventoryItemRepository;
  @Mock private InventoryMovementRepository inventoryMovementRepository;
  @Mock private SaleMapper saleMapper;

  @InjectMocks private SaleService saleService;

private UUID saleId;
  private UUID editionId;
  private Sale sale;
  private SaleResponse saleResponse;

  @BeforeEach
  void setUp() {
saleId = UUID.randomUUID();
    editionId = UUID.randomUUID();

    sale =
        Sale.builder()
            .id(saleId)
            .status(SaleStatus.PENDING)
            .saleItems(new ArrayList<>())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    saleResponse =
        SaleResponse.builder()
            .id(saleId)
            .status(SaleStatus.PENDING)
            .items(List.of())
            .total(BigDecimal.ZERO)
            .build();
  }

  @Test
  void createSale_ShouldReturnCreatedSale() {
given(saleRepository.save(any(Sale.class))).willReturn(sale);
    given(saleMapper.toRest(sale)).willReturn(saleResponse);

    SaleResponse result = saleService.createSale(null);

    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(SaleStatus.PENDING);
    then(saleRepository).should().save(any(Sale.class));
  }

  @Test
  void getSale_ShouldReturnSale() {
    given(saleRepository.findById(saleId)).willReturn(Optional.of(sale));
    given(saleMapper.toRest(sale)).willReturn(saleResponse);

    SaleResponse result = saleService.getSale(saleId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(saleId);
  }

  @Test
  void getSale_WhenNotFound_ShouldThrow() {
    given(saleRepository.findById(saleId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.getSale(saleId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Sale not found");
  }

  @Test
void getAllSales_ShouldReturnPage() {
    PageRequest pageable = PageRequest.of(0, 20);
    Page<Sale> salePage = new PageImpl<>(List.of(sale), pageable, 1);

    given(saleRepository.findAll(pageable)).willReturn(salePage);
    given(saleMapper.toRest(sale)).willReturn(saleResponse);

    Page<SaleResponse> result = saleService.getAllSales(pageable);

    assertThat(result).hasSize(1);
  }

  @Test
  void cancelSale_ShouldSetCancelled() {
    given(saleRepository.findById(saleId)).willReturn(Optional.of(sale));
    given(saleRepository.save(any(Sale.class))).willReturn(sale);
    SaleResponse cancelledResponse =
        SaleResponse.builder().id(saleId).status(SaleStatus.CANCELLED).build();
    given(saleMapper.toRest(any(Sale.class))).willReturn(cancelledResponse);

    SaleResponse result = saleService.cancelSale(saleId);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.CANCELLED);
  }

  @Test
  void cancelSale_WhenNotPending_ShouldThrow() {
    sale.setStatus(SaleStatus.PAID);
    given(saleRepository.findById(saleId)).willReturn(Optional.of(sale));

    assertThatThrownBy(() -> saleService.cancelSale(saleId))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("not in PENDING status");
  }
}
