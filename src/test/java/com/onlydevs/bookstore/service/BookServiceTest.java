package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookMapper;
import com.onlydevs.bookstore.model.*;
import com.onlydevs.bookstore.model.dto.request.CreateBookRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookRequest;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.enums.AuthorRole;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import com.onlydevs.bookstore.repository.BookAuthorRepository;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.GenreRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @Mock private BookMapper bookMapper;

  @Mock private AuthorRepository authorRepository;

  @Mock private GenreRepository genreRepository;

  @Mock private BookAuthorRepository bookAuthorRepository;

  @Mock private BookEditionRepository bookEditionRepository;

  @Mock private InventoryItemRepository inventoryItemRepository;

  @InjectMocks private BookService bookService;

  private Pageable pageable;
  private Book bookWithAuthor;
  private Book bookWithoutAuthor;
  private Book bookWithMultipleAuthors;
  private BookSummaryResponse responseWithAuthor;
  private BookSummaryResponse responseWithoutAuthor;
  private BookSummaryResponse responseWithMultipleAuthors;
  private BookDetailResponse bookDetailResponse;
  private Author author1;
  private Author author2;
  private Genre genre;
  private UUID bookId;
  private UUID authorId;
  private UUID genreId;
  private UUID editionId;
  private Book book;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 20);
    bookId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    genreId = UUID.randomUUID();
    editionId = UUID.randomUUID();

    author1 =
        Author.builder().id(UUID.randomUUID()).firstName("Scott").lastName("Fitzgerald").build();

    author2 =
        Author.builder().id(UUID.randomUUID()).firstName("Ernest").lastName("Hemingway").build();

    genre = Genre.builder().id(genreId).name("Fiction").build();

    BookAuthor bookAuthor =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author1)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();

    bookWithAuthor =
        Book.builder()
            .id(UUID.randomUUID())
            .title("The Great Gatsby")
            .summary("A story about the American dream")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/gatsby.jpg")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of(bookAuthor))
            .genres(Set.of(genre))
            .build();

    responseWithAuthor =
        BookSummaryResponse.builder()
            .id(bookWithAuthor.getId())
            .title("The Great Gatsby")
            .language("ENGLISH")
            .coverUrl("https://example.com/gatsby.jpg")
            .authorNames(List.of("Scott Fitzgerald"))
            .genreNames(List.of("Fiction"))
            .createdAt(bookWithAuthor.getCreatedAt())
            .build();

    bookWithoutAuthor =
        Book.builder()
            .id(UUID.randomUUID())
            .title("1984")
            .summary("A dystopian social science fiction")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/1984.jpg")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of())
            .genres(Set.of())
            .build();

    responseWithoutAuthor =
        BookSummaryResponse.builder()
            .id(bookWithoutAuthor.getId())
            .title("1984")
            .language("ENGLISH")
            .coverUrl("https://example.com/1984.jpg")
            .authorNames(List.of())
            .genreNames(List.of())
            .createdAt(bookWithoutAuthor.getCreatedAt())
            .build();

    BookAuthor bookAuthor1 =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author2)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(2)
            .build();

    BookAuthor bookAuthor2 =
        BookAuthor.builder()
            .id(UUID.randomUUID())
            .author(author1)
            .role(AuthorRole.AUTHOR)
            .contributionOrder(1)
            .build();

    bookWithMultipleAuthors =
        Book.builder()
            .id(UUID.randomUUID())
            .title("The Sun Also Rises")
            .summary("A novel about expatriates")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/sun.jpg")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of(bookAuthor1, bookAuthor2))
            .genres(Set.of())
            .build();

    responseWithMultipleAuthors =
        BookSummaryResponse.builder()
            .id(bookWithMultipleAuthors.getId())
            .title("The Sun Also Rises")
            .language("ENGLISH")
            .coverUrl("https://example.com/sun.jpg")
            .authorNames(List.of("Scott Fitzgerald", "Ernest Hemingway"))
            .genreNames(List.of())
            .createdAt(bookWithMultipleAuthors.getCreatedAt())
            .build();

    book =
        Book.builder()
            .id(bookId)
            .title("Test Book")
            .summary("Test summary")
            .language(BookLanguage.ENGLISH)
            .coverUrl("https://example.com/cover.jpg")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of())
            .genres(new HashSet<>())
            .build();

    bookDetailResponse =
        BookDetailResponse.builder()
            .id(bookId)
            .title("Test Book")
            .summary("Test summary")
            .language("ENGLISH")
            .coverUrl("https://example.com/cover.jpg")
            .authors(List.of())
            .genres(List.of())
            .createdAt(book.getCreatedAt())
            .updatedAt(book.getUpdatedAt())
            .build();
  }

  @Test
  void getAllBooks_ShouldReturnPageOfBookSummaryResponses() {
    Page<Book> bookPage = new PageImpl<>(List.of(bookWithAuthor, bookWithoutAuthor), pageable, 2);
    given(bookRepository.findAll(pageable)).willReturn(bookPage);
    given(bookMapper.toBookSummaryResponse(bookWithAuthor)).willReturn(responseWithAuthor);
    given(bookMapper.toBookSummaryResponse(bookWithoutAuthor)).willReturn(responseWithoutAuthor);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(20);
    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.isLast()).isTrue();

    BookSummaryResponse firstBook = result.getContent().get(0);
    assertThat(firstBook.getId()).isEqualTo(bookWithAuthor.getId());
    assertThat(firstBook.getTitle()).isEqualTo("The Great Gatsby");
    assertThat(firstBook.getLanguage()).isEqualTo("ENGLISH");
    assertThat(firstBook.getCoverUrl()).isEqualTo("https://example.com/gatsby.jpg");
    assertThat(firstBook.getAuthorNames()).hasSize(1);
    assertThat(firstBook.getAuthorNames()).contains("Scott Fitzgerald");
    assertThat(firstBook.getGenreNames()).hasSize(1);
    assertThat(firstBook.getGenreNames()).contains("Fiction");
    assertThat(firstBook.getCreatedAt()).isNotNull();

    BookSummaryResponse secondBook = result.getContent().get(1);
    assertThat(secondBook.getId()).isEqualTo(bookWithoutAuthor.getId());
    assertThat(secondBook.getTitle()).isEqualTo("1984");
    assertThat(secondBook.getAuthorNames()).isEmpty();
    assertThat(secondBook.getGenreNames()).isEmpty();

    then(bookRepository).should().findAll(pageable);
    then(bookMapper).should().toBookSummaryResponse(bookWithAuthor);
    then(bookMapper).should().toBookSummaryResponse(bookWithoutAuthor);
  }

  @Test
  void getAllBooks_WhenNoBooks_ShouldReturnEmptyPage() {
    Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);
    given(bookRepository.findAll(pageable)).willReturn(emptyPage);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getTotalPages()).isEqualTo(0);
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(20);
    assertThat(result.isLast()).isTrue();

    then(bookRepository).should().findAll(pageable);
    then(bookMapper).should(never()).toBookSummaryResponse(any());
  }

  @Test
  void getAllBooks_WhenBookHasNoAuthors_ShouldReturnEmptyAuthorList() {
    Page<Book> bookPage = new PageImpl<>(List.of(bookWithoutAuthor), pageable, 1);
    given(bookRepository.findAll(pageable)).willReturn(bookPage);
    given(bookMapper.toBookSummaryResponse(bookWithoutAuthor)).willReturn(responseWithoutAuthor);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getAuthorNames()).isEmpty();
    assertThat(result.getContent().getFirst().getGenreNames()).isEmpty();

    then(bookRepository).should().findAll(pageable);
  }

  @Test
  void getAllBooks_WhenBookHasMultipleAuthors_ShouldReturnAuthorsSortedByContributionOrder() {
    Page<Book> bookPage = new PageImpl<>(List.of(bookWithMultipleAuthors), pageable, 1);
    given(bookRepository.findAll(pageable)).willReturn(bookPage);
    given(bookMapper.toBookSummaryResponse(bookWithMultipleAuthors))
        .willReturn(responseWithMultipleAuthors);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result.getContent()).hasSize(1);
    List<String> authorNames = result.getContent().getFirst().getAuthorNames();
    assertThat(authorNames).hasSize(2);
    assertThat(authorNames.get(0)).isEqualTo("Scott Fitzgerald");
    assertThat(authorNames.get(1)).isEqualTo("Ernest Hemingway");

    then(bookRepository).should().findAll(pageable);
  }

  @Test
  void getAllBooks_WhenBookHasNullLanguage_ShouldReturnNullLanguage() {
    Book bookWithNullLanguage =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Book Without Language")
            .summary("Test summary")
            .language(null)
            .coverUrl(null)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of())
            .genres(Set.of())
            .build();

    BookSummaryResponse responseWithNullLanguage =
        BookSummaryResponse.builder()
            .id(bookWithNullLanguage.getId())
            .title("Book Without Language")
            .language(null)
            .coverUrl(null)
            .authorNames(List.of())
            .genreNames(List.of())
            .createdAt(bookWithNullLanguage.getCreatedAt())
            .build();

    Page<Book> bookPage = new PageImpl<>(List.of(bookWithNullLanguage), pageable, 1);
    given(bookRepository.findAll(pageable)).willReturn(bookPage);
    given(bookMapper.toBookSummaryResponse(bookWithNullLanguage))
        .willReturn(responseWithNullLanguage);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getLanguage()).isNull();

    then(bookRepository).should().findAll(pageable);
  }

  @Test
  void getAllBooks_WhenBookHasNullCoverUrl_ShouldReturnNullCoverUrl() {
    Book bookWithNullCover =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Book Without Cover")
            .summary("Test summary")
            .language(BookLanguage.ENGLISH)
            .coverUrl(null)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .bookAuthors(Set.of())
            .genres(Set.of())
            .build();

    BookSummaryResponse responseWithNullCover =
        BookSummaryResponse.builder()
            .id(bookWithNullCover.getId())
            .title("Book Without Cover")
            .language("ENGLISH")
            .coverUrl(null)
            .authorNames(List.of())
            .genreNames(List.of())
            .createdAt(bookWithNullCover.getCreatedAt())
            .build();

    Page<Book> bookPage = new PageImpl<>(List.of(bookWithNullCover), pageable, 1);
    given(bookRepository.findAll(pageable)).willReturn(bookPage);
    given(bookMapper.toBookSummaryResponse(bookWithNullCover)).willReturn(responseWithNullCover);

    Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getCoverUrl()).isNull();

    then(bookRepository).should().findAll(pageable);
  }

  @Test
  void getAllBooks_ShouldUseCorrectPageableParameters() {
    Pageable customPageable = PageRequest.of(2, 15);
    Page<Book> emptyPage = new PageImpl<>(List.of(), customPageable, 0);
    given(bookRepository.findAll(customPageable)).willReturn(emptyPage);

    bookService.getAllBooks(customPageable);

    then(bookRepository).should().findAll(customPageable);
  }

  @Test
  void getAllBooks_WhenRepositoryThrowsException_ShouldPropagateException() {
    RuntimeException exception = new RuntimeException("Database connection failed");
    given(bookRepository.findAll(pageable)).willThrow(exception);

    assertThatThrownBy(() -> bookService.getAllBooks(pageable))
        .isInstanceOf(RuntimeException.class);

    then(bookRepository).should().findAll(pageable);
    then(bookMapper).should(never()).toBookSummaryResponse(any());
  }

  @Test
  void getBookById_WhenBookExists_ShouldReturnDetail() {
    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(bookMapper.toBookDetailResponse(book)).willReturn(bookDetailResponse);

    BookDetailResponse result = bookService.getBookById(bookId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(bookId);
    assertThat(result.getTitle()).isEqualTo("Test Book");
    then(bookRepository).should().findById(bookId);
  }

  @Test
  void getBookById_WhenBookNotFound_ShouldThrow() {
    given(bookRepository.findById(bookId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.getBookById(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found");

    then(bookRepository).should().findById(bookId);
  }

  @Test
  void createBook_ShouldReturnCreatedDetail() {
    CreateBookRequest request =
        CreateBookRequest.builder()
            .title("New Book")
            .summary("New summary")
            .language("ENGLISH")
            .build();
    given(bookRepository.save(any(Book.class))).willReturn(book);
    given(bookMapper.toBookDetailResponse(book)).willReturn(bookDetailResponse);

    BookDetailResponse result = bookService.createBook(request);

    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Book");
    then(bookRepository).should().save(any(Book.class));
  }

  @Test
  void createBook_WithInvalidLanguage_ShouldThrow() {
    CreateBookRequest request =
        CreateBookRequest.builder().title("New Book").language("INVALID_LANG").build();

    assertThatThrownBy(() -> bookService.createBook(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Invalid language");

    then(bookRepository).should(never()).save(any());
  }

  @Test
  void updateBook_WhenBookExists_ShouldUpdateFields() {
    UpdateBookRequest request =
        UpdateBookRequest.builder().title("Updated Title").summary("Updated summary").build();
    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(bookRepository.save(any(Book.class))).willReturn(book);
    given(bookMapper.toBookDetailResponse(any())).willReturn(bookDetailResponse);

    BookDetailResponse result = bookService.updateBook(bookId, request);

    assertThat(result).isNotNull();
    then(bookRepository).should().findById(bookId);
    then(bookRepository).should().save(any(Book.class));
  }

  @Test
  void updateBook_WhenBookNotFound_ShouldThrow() {
    UpdateBookRequest request = UpdateBookRequest.builder().title("Updated").build();
    given(bookRepository.findById(bookId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(NotFoundException.class);

    then(bookRepository).should().findById(bookId);
    then(bookRepository).should(never()).save(any());
  }

  @Test
  void deleteBook_WhenBookExists_ShouldDelete() {
    given(bookRepository.existsById(bookId)).willReturn(true);

    bookService.deleteBook(bookId);

    then(bookRepository).should().existsById(bookId);
    then(bookRepository).should().deleteById(bookId);
  }

  @Test
  void deleteBook_WhenBookNotFound_ShouldThrow() {
    given(bookRepository.existsById(bookId)).willReturn(false);

    assertThatThrownBy(() -> bookService.deleteBook(bookId)).isInstanceOf(NotFoundException.class);

    then(bookRepository).should().existsById(bookId);
    then(bookRepository).should(never()).deleteById(any());
  }

  @Test
  void addAuthorToBook_WhenNotLinked_ShouldAdd() {
    BookAuthor bookAuthor =
        BookAuthor.builder().id(UUID.randomUUID()).book(book).author(author1).build();
    BookAuthorResponse authorResponse =
        BookAuthorResponse.builder()
            .id(bookAuthor.getId())
            .bookId(bookId)
            .authorId(author1.getId())
            .authorFullName("Scott Fitzgerald")
            .build();

    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(authorRepository.findById(author1.getId())).willReturn(Optional.of(author1));
    given(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, author1.getId()))
        .willReturn(false);
    given(bookAuthorRepository.save(any(BookAuthor.class))).willReturn(bookAuthor);
    given(bookMapper.toBookAuthorResponse(any(BookAuthor.class))).willReturn(authorResponse);

    BookAuthorResponse result = bookService.addAuthorToBook(bookId, author1.getId());

    assertThat(result).isNotNull();
    assertThat(result.getAuthorFullName()).isEqualTo("Scott Fitzgerald");
    then(bookAuthorRepository).should().save(any(BookAuthor.class));
  }

  @Test
  void addAuthorToBook_WhenAlreadyLinked_ShouldThrow() {
    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(authorRepository.findById(author1.getId())).willReturn(Optional.of(author1));
    given(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, author1.getId())).willReturn(true);

    assertThatThrownBy(() -> bookService.addAuthorToBook(bookId, author1.getId()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("already linked");

    then(bookAuthorRepository).should(never()).save(any());
  }

  @Test
  void removeAuthorFromBook_WhenLinked_ShouldRemove() {
    BookAuthor bookAuthor =
        BookAuthor.builder().id(UUID.randomUUID()).book(book).author(author1).build();
    given(bookAuthorRepository.findByBookIdAndAuthorId(bookId, author1.getId()))
        .willReturn(Optional.of(bookAuthor));

    bookService.removeAuthorFromBook(bookId, author1.getId());

    then(bookAuthorRepository).should().delete(bookAuthor);
  }

  @Test
  void removeAuthorFromBook_WhenNotLinked_ShouldThrow() {
    given(bookAuthorRepository.findByBookIdAndAuthorId(bookId, author1.getId()))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.removeAuthorFromBook(bookId, author1.getId()))
        .isInstanceOf(NotFoundException.class);

    then(bookAuthorRepository).should(never()).delete(any());
  }

  @Test
  void addGenreToBook_ShouldAddGenre() {
    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));

    bookService.addGenreToBook(bookId, genreId);

    assertThat(book.getGenres()).contains(genre);
    then(bookRepository).should().save(book);
  }

  @Test
  void removeGenreFromBook_ShouldRemoveGenre() {
    book.getGenres().add(genre);
    given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
    given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));

    bookService.removeGenreFromBook(bookId, genreId);

    assertThat(book.getGenres()).doesNotContain(genre);
    then(bookRepository).should().save(book);
  }

  @Test
  void getEditionStock_withValidBookAndEdition_shouldReturnCorrectStock() {
    BookEdition edition = BookEdition.builder().id(editionId).book(book).build();
    InventoryItem item1 = InventoryItem.builder().quantityOnHand(5).build();
    InventoryItem item2 = InventoryItem.builder().quantityOnHand(3).build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));
    given(inventoryItemRepository.findByBookEditionId(editionId)).willReturn(List.of(item1, item2));

    Integer stock = bookService.getEditionStock(bookId, editionId);

    assertThat(stock).isEqualTo(8);
  }

  @Test
  void getEditionStock_withNonExistingEdition_shouldThrow404() {
    given(bookEditionRepository.findById(editionId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.getEditionStock(bookId, editionId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Edition not found");
  }

  @Test
  void getEditionStock_withEditionBelongingToAnotherBook_shouldThrow404() {
    Book otherBook = Book.builder().id(UUID.randomUUID()).title("Other Book").build();
    BookEdition edition = BookEdition.builder().id(editionId).book(otherBook).build();

    given(bookEditionRepository.findById(editionId)).willReturn(Optional.of(edition));

    assertThatThrownBy(() -> bookService.getEditionStock(bookId, editionId))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Edition does not belong to book");
  }
}
