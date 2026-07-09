package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookPriceHistoryMapper;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.BookPriceHistory;
import com.onlydevs.bookstore.model.dto.request.CreateBookPriceRequest;
import com.onlydevs.bookstore.model.dto.response.BookPriceResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookPriceHistoryRepository;
import java.math.BigDecimal;
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
class BookPriceHistoryServiceTest {

  @Mock private BookPriceHistoryRepository bookPriceHistoryRepository;
  @Mock private BookEditionRepository bookEditionRepository;
  @Mock private BookPriceHistoryMapper bookPriceHistoryMapper;

  @InjectMocks private BookPriceHistoryService bookPriceHistoryService;

  private UUID editionId;
  private BookEdition edition;
  private BookPriceHistory price;
  private BookPriceResponse priceResponse;
  private CreateBookPriceRequest createRequest;

  @BeforeEach
  void setUp() {
    editionId = UUID.randomUUID();

    edition = BookEdition.builder().id(editionId).build();

    price =
        BookPriceHistory.builder()
            .id(UUID.randomUUID())
            .price(new BigDecimal("19.99"))
            .effectiveFrom(Instant.now())
            .bookEdition(edition)
            .build();

    priceResponse =
        BookPriceResponse.builder()
            .id(price.getId())
            .editionId(editionId)
            .price(new BigDecimal("19.99"))
            .effectiveFrom(price.getEffectiveFrom())
            .build();

    createRequest =
        CreateBookPriceRequest.builder()
            .price(new BigDecimal("24.99"))
            .effectiveFrom(Instant.now())
            .build();
  }

  @Test
  void getPrices_WhenEditionExists_ShouldReturnList() {
    given(bookEditionRepository.existsById(editionId)).willReturn(true);
    given(bookPriceHistoryRepository.findByBookEditionIdOrderByEffectiveFromDesc(editionId))
        .willReturn(List.of(price));
    given(bookPriceHistoryMapper.toResponseList(List.of(price)))
        .willReturn(List.of(priceResponse));

    List<BookPriceResponse> result = bookPriceHistoryService.getPrices(editionId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
    then(bookEditionRepository).should().existsById(editionId);
  }

  @Test
  void getPrices_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.existsById(editionId)).willReturn(false);

    assertThatThrownBy(() -> bookPriceHistoryService.getPrices(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookPriceHistoryRepository).should(never()).findByBookEditionIdOrderByEffectiveFromDesc(any());
  }

  @Test
  void getCurrentPrice_WhenPriceExists_ShouldReturnPrice() {
    given(bookEditionRepository.existsById(editionId)).willReturn(true);
    given(bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId))
        .willReturn(Optional.of(price));
    given(bookPriceHistoryMapper.toResponse(price)).willReturn(priceResponse);

    BookPriceResponse result = bookPriceHistoryService.getCurrentPrice(editionId);

    assertThat(result).isNotNull();
    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
    then(bookEditionRepository).should().existsById(editionId);
  }

  @Test
  void getCurrentPrice_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.existsById(editionId)).willReturn(false);

    assertThatThrownBy(() -> bookPriceHistoryService.getCurrentPrice(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");
  }

  @Test
  void getCurrentPrice_WhenNoPrice_ShouldThrow() {
    given(bookEditionRepository.existsById(editionId)).willReturn(true);
    given(bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> bookPriceHistoryService.getCurrentPrice(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No price found");
  }

  @Test
  void createPrice_WhenValid_ShouldReturnCreated() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId))
        .willReturn(Optional.empty());
    given(bookPriceHistoryRepository.save(any(BookPriceHistory.class))).willReturn(price);
    given(bookPriceHistoryMapper.toResponse(price)).willReturn(priceResponse);

    BookPriceResponse result = bookPriceHistoryService.createPrice(editionId, createRequest);

    assertThat(result).isNotNull();
    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
    then(bookPriceHistoryRepository).should().save(any(BookPriceHistory.class));
  }

  @Test
  void createPrice_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookPriceHistoryService.createPrice(editionId, createRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookPriceHistoryRepository).should(never()).save(any());
  }

  @Test
  void createPrice_WhenPreviousPriceExists_ShouldClosePrevious() {
    BookPriceHistory previousPrice =
        BookPriceHistory.builder()
            .id(UUID.randomUUID())
            .price(new BigDecimal("19.99"))
            .effectiveFrom(Instant.now().minusSeconds(3600))
            .bookEdition(edition)
            .build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookPriceHistoryRepository
        .findFirstByBookEditionIdAndEffectiveToIsNullOrderByEffectiveFromDesc(editionId))
        .willReturn(Optional.of(previousPrice));
    given(bookPriceHistoryRepository.save(any(BookPriceHistory.class))).willReturn(price);
    given(bookPriceHistoryMapper.toResponse(price)).willReturn(priceResponse);

    BookPriceResponse result = bookPriceHistoryService.createPrice(editionId, createRequest);

    assertThat(result).isNotNull();
    assertThat(previousPrice.getEffectiveTo()).isEqualTo(createRequest.getEffectiveFrom());
    then(bookPriceHistoryRepository).should(times(2)).save(any(BookPriceHistory.class));
  }
}
