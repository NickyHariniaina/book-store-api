package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookEditionMapper;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.request.CreateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.PublisherRepository;
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
class BookEditionServiceTest {

  @Mock private BookEditionRepository bookEditionRepository;
  @Mock private BookRepository bookRepository;
  @Mock private PublisherRepository publisherRepository;
  @Mock private BookEditionMapper bookEditionMapper;

  @InjectMocks private BookEditionService bookEditionService;

  private UUID bookId;
  private UUID editionId;
  private UUID publisherId;
  private Book book;
  private BookEdition edition;
  private Publisher publisher;
  private BookEditionResponse editionResponse;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    editionId = UUID.randomUUID();
    publisherId = UUID.randomUUID();

    publisher =
        Publisher.builder()
            .id(publisherId)
            .name("Test Publisher")
            .website("https://example.com")
            .email("publisher@example.com")
            .phone("123456789")
            .country("USA")
            .createdAt(Instant.now())
            .build();

    book = Book.builder().id(bookId).title("Test Book").createdAt(Instant.now()).build();

    edition =
        BookEdition.builder()
            .id(editionId)
            .isbn("9783161484100")
            .edition("1st")
            .format(BookFormat.PAPERBACK)
            .active(true)
            .book(book)
            .publisher(publisher)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    PublisherResponse publisherResponse =
        PublisherResponse.builder()
            .id(publisherId)
            .name("Test Publisher")
            .website("https://example.com")
            .email("publisher@example.com")
            .phone("123456789")
            .country("USA")
            .createdAt(publisher.getCreatedAt())
            .build();

    editionResponse =
        BookEditionResponse.builder()
            .id(editionId)
            .bookId(bookId)
            .bookTitle("Test Book")
            .publisher(publisherResponse)
            .isbn("9783161484100")
            .edition("1st")
            .format("PAPERBACK")
            .active(true)
            .createdAt(edition.getCreatedAt())
            .updatedAt(edition.getUpdatedAt())
            .build();
  }

  @Test
  void getEditionsByBookId_WhenBookExists_ShouldReturnEditions() {
    given(bookRepository.existsById(bookId)).willReturn(true);
    given(bookEditionRepository.findByBookId(bookId)).willReturn(List.of(edition));
    given(bookEditionMapper.toBookEditionResponseList(List.of(edition)))
        .willReturn(List.of(editionResponse));

    List<BookEditionResponse> result = bookEditionService.getEditionsByBookId(bookId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getIsbn()).isEqualTo("9783161484100");
    then(bookRepository).should().existsById(bookId);
    then(bookEditionRepository).should().findByBookId(bookId);
  }

  @Test
  void getEditionsByBookId_WhenBookNotFound_ShouldThrow() {
    given(bookRepository.existsById(bookId)).willReturn(false);

    assertThatThrownBy(() -> bookEditionService.getEditionsByBookId(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found");

    then(bookEditionRepository).should(never()).findByBookId(any());
  }

  @Test
  void getEditionById_WhenEditionExists_ShouldReturnEdition() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.getEditionById(editionId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(editionId);
    then(bookEditionRepository).should().findById(editionId);
  }

  @Test
  void getEditionById_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.getEditionById(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookEditionRepository).should().findById(editionId);
  }

  @Test
  void getEditionByIsbn_WhenEditionExists_ShouldReturnEdition() {
    given(bookEditionRepository.findByIsbn("9783161484100")).willReturn(Optional.of(edition));
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.getEditionByIsbn("9783161484100");

    assertThat(result).isNotNull();
    assertThat(result.getIsbn()).isEqualTo("9783161484100");
    then(bookEditionRepository).should().findByIsbn("9783161484100");
  }

  @Test
  void getEditionByIsbn_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.findByIsbn("9783161484100")).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.getEditionByIsbn("9783161484100"))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookEditionRepository).should().findByIsbn("9783161484100");
  }

  @Test
  void createEdition_WhenValid_ShouldReturnCreated() {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder()
            .publisherId(publisherId)
            .isbn("9783161484100")
            .edition("1st")
            .format("PAPERBACK")
            .build();

    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(bookEditionRepository.existsByIsbn("9783161484100")).willReturn(false);
    given(bookEditionRepository.save(any(BookEdition.class))).willReturn(edition);
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.createEdition(bookId, request);

    assertThat(result).isNotNull();
    assertThat(result.getIsbn()).isEqualTo("9783161484100");
    then(bookEditionRepository).should().save(any(BookEdition.class));
  }

  @Test
  void createEdition_WhenBookNotFound_ShouldThrow() {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder().publisherId(publisherId).isbn("9783161484100").build();

    given(bookRepository.findById(bookId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.createEdition(bookId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void createEdition_WhenPublisherNotFound_ShouldThrow() {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder().publisherId(publisherId).isbn("9783161484100").build();

    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.createEdition(bookId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Publisher not found");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void createEdition_WhenIsbnAlreadyExists_ShouldThrow() {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder().publisherId(publisherId).isbn("9783161484100").build();

    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(bookEditionRepository.existsByIsbn("9783161484100")).willReturn(true);

    assertThatThrownBy(() -> bookEditionService.createEdition(bookId, request))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("ISBN already exists");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void createEdition_WithInvalidFormat_ShouldThrow() {
    CreateBookEditionRequest request =
        CreateBookEditionRequest.builder()
            .publisherId(publisherId)
            .isbn("9783161484100")
            .format("INVALID")
            .build();

    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
    given(bookEditionRepository.existsByIsbn("9783161484100")).willReturn(false);

    assertThatThrownBy(() -> bookEditionService.createEdition(bookId, request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Invalid format");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void updateEdition_WhenValid_ShouldReturnUpdated() {
    UpdateBookEditionRequest request =
        UpdateBookEditionRequest.builder()
            .isbn("9783161484101")
            .edition("2nd")
            .format("HARDCOVER")
            .build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionRepository.save(any(BookEdition.class))).willReturn(edition);
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.updateEdition(editionId, request);

    assertThat(result).isNotNull();
    then(bookEditionRepository).should().save(any(BookEdition.class));
  }

  @Test
  void updateEdition_WhenEditionNotFound_ShouldThrow() {
    UpdateBookEditionRequest request =
        UpdateBookEditionRequest.builder().isbn("978-3-16-148410-1").build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.updateEdition(editionId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void updateEdition_WhenIsbnAlreadyExistsOnDifferentEdition_ShouldThrow() {
    UpdateBookEditionRequest request =
        UpdateBookEditionRequest.builder().isbn("978-3-16-148410-1").build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionRepository.existsByIsbn("978-3-16-148410-1")).willReturn(true);

    assertThatThrownBy(() -> bookEditionService.updateEdition(editionId, request))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("ISBN already exists");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void updateEdition_WhenSameIsbn_ShouldNotCheckDuplicate() {
    UpdateBookEditionRequest request =
        UpdateBookEditionRequest.builder().isbn("9783161484100").build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionRepository.save(any(BookEdition.class))).willReturn(edition);
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.updateEdition(editionId, request);

    assertThat(result).isNotNull();
    then(bookEditionRepository).should(never()).existsByIsbn(any());
  }

  @Test
  void activateEdition_WhenEditionExists_ShouldActivate() {
    edition.setActive(false);
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionRepository.save(any(BookEdition.class))).willReturn(edition);
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.activateEdition(editionId);

    assertThat(result).isNotNull();
    assertThat(edition.getActive()).isTrue();
    then(bookEditionRepository).should().save(edition);
  }

  @Test
  void activateEdition_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.activateEdition(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookEditionRepository).should(never()).save(any());
  }

  @Test
  void deactivateEdition_WhenEditionExists_ShouldDeactivate() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(bookEditionRepository.save(any(BookEdition.class))).willReturn(edition);
    given(bookEditionMapper.toBookEditionResponse(edition)).willReturn(editionResponse);

    BookEditionResponse result = bookEditionService.deactivateEdition(editionId);

    assertThat(result).isNotNull();
    assertThat(edition.getActive()).isFalse();
    then(bookEditionRepository).should().save(edition);
  }

  @Test
  void deactivateEdition_WhenEditionNotFound_ShouldThrow() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookEditionService.deactivateEdition(editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");

    then(bookEditionRepository).should(never()).save(any());
  }
}
