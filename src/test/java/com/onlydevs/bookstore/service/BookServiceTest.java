package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookMapper;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.*;
import com.onlydevs.bookstore.model.enums.AuthorRole;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.repository.BookRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Pageable pageable;
    private Book bookWithAuthor;
    private Book bookWithoutAuthor;
    private Book bookWithMultipleAuthors;
    private BookSummaryResponse responseWithAuthor;
    private BookSummaryResponse responseWithoutAuthor;
    private BookSummaryResponse responseWithMultipleAuthors;
    private Author author1;
    private Author author2;
    private Genre genre;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);

        author1 = Author.builder()
                .id(UUID.randomUUID())
                .firstName("Scott")
                .lastName("Fitzgerald")
                .build();

        author2 = Author.builder()
                .id(UUID.randomUUID())
                .firstName("Ernest")
                .lastName("Hemingway")
                .build();

        genre = Genre.builder()
                .id(UUID.randomUUID())
                .name("Fiction")
                .build();

        BookAuthor bookAuthor = BookAuthor.builder()
                .id(UUID.randomUUID())
                .author(author1)
                .role(AuthorRole.AUTHOR)
                .contributionOrder(1)
                .build();

        bookWithAuthor = Book.builder()
                .id(UUID.randomUUID())
                .title("The Great Gatsby")
                .summary("A story about the American dream")
                .language(BookLanguage.ENGLISH)
                .coverUrl("https://example.com/gatsby.jpg")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .bookAuthors(List.of(bookAuthor))
                .genres(Set.of(genre))
                .build();

        responseWithAuthor = BookSummaryResponse.builder()
                .id(bookWithAuthor.getId())
                .title("The Great Gatsby")
                .language("ENGLISH")
                .coverUrl("https://example.com/gatsby.jpg")
                .authorNames(List.of("Scott Fitzgerald"))
                .genreNames(List.of("Fiction"))
                .createdAt(bookWithAuthor.getCreatedAt())
                .build();

        bookWithoutAuthor = Book.builder()
                .id(UUID.randomUUID())
                .title("1984")
                .summary("A dystopian social science fiction")
                .language(BookLanguage.ENGLISH)
                .coverUrl("https://example.com/1984.jpg")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .bookAuthors(List.of())
                .genres(Set.of())
                .build();

        responseWithoutAuthor = BookSummaryResponse.builder()
                .id(bookWithoutAuthor.getId())
                .title("1984")
                .language("ENGLISH")
                .coverUrl("https://example.com/1984.jpg")
                .authorNames(List.of())
                .genreNames(List.of())
                .createdAt(bookWithoutAuthor.getCreatedAt())
                .build();

        BookAuthor bookAuthor1 = BookAuthor.builder()
                .id(UUID.randomUUID())
                .author(author2)
                .role(AuthorRole.AUTHOR)
                .contributionOrder(2)
                .build();

        BookAuthor bookAuthor2 = BookAuthor.builder()
                .id(UUID.randomUUID())
                .author(author1)
                .role(AuthorRole.AUTHOR)
                .contributionOrder(1)
                .build();

        bookWithMultipleAuthors = Book.builder()
                .id(UUID.randomUUID())
                .title("The Sun Also Rises")
                .summary("A novel about expatriates")
                .language(BookLanguage.ENGLISH)
                .coverUrl("https://example.com/sun.jpg")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .bookAuthors(List.of(bookAuthor1, bookAuthor2))
                .genres(Set.of())
                .build();

        responseWithMultipleAuthors = BookSummaryResponse.builder()
                .id(bookWithMultipleAuthors.getId())
                .title("The Sun Also Rises")
                .language("ENGLISH")
                .coverUrl("https://example.com/sun.jpg")
                .authorNames(List.of("Scott Fitzgerald", "Ernest Hemingway"))
                .genreNames(List.of())
                .createdAt(bookWithMultipleAuthors.getCreatedAt())
                .build();
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBookSummaryResponses() {
        Page<Book> bookPage = new PageImpl<>(List.of(bookWithAuthor, bookWithoutAuthor), pageable, 2);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toBookSummaryResponse(bookWithAuthor)).thenReturn(responseWithAuthor);
        when(bookMapper.toBookSummaryResponse(bookWithoutAuthor)).thenReturn(responseWithoutAuthor);

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

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toBookSummaryResponse(bookWithAuthor);
        verify(bookMapper, times(1)).toBookSummaryResponse(bookWithoutAuthor);
    }

    @Test
    void getAllBooks_WhenNoBooks_ShouldReturnEmptyPage() {
        Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.isLast()).isTrue();

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, never()).toBookSummaryResponse(any());
    }

    @Test
    void getAllBooks_WhenBookHasNoAuthors_ShouldReturnEmptyAuthorList() {
        Page<Book> bookPage = new PageImpl<>(List.of(bookWithoutAuthor), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toBookSummaryResponse(bookWithoutAuthor)).thenReturn(responseWithoutAuthor);

        Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAuthorNames()).isEmpty();
        assertThat(result.getContent().getFirst().getGenreNames()).isEmpty();

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllBooks_WhenBookHasMultipleAuthors_ShouldReturnAuthorsSortedByContributionOrder() {
        Page<Book> bookPage = new PageImpl<>(List.of(bookWithMultipleAuthors), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toBookSummaryResponse(bookWithMultipleAuthors)).thenReturn(responseWithMultipleAuthors);

        Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        List<String> authorNames = result.getContent().getFirst().getAuthorNames();
        assertThat(authorNames).hasSize(2);
        assertThat(authorNames.get(0)).isEqualTo("Scott Fitzgerald");
        assertThat(authorNames.get(1)).isEqualTo("Ernest Hemingway");

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllBooks_WhenBookHasNullLanguage_ShouldReturnNullLanguage() {
        Book bookWithNullLanguage = Book.builder()
                .id(UUID.randomUUID())
                .title("Book Without Language")
                .summary("Test summary")
                .language(null)
                .coverUrl(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .bookAuthors(List.of())
                .genres(Set.of())
                .build();

        BookSummaryResponse responseWithNullLanguage = BookSummaryResponse.builder()
                .id(bookWithNullLanguage.getId())
                .title("Book Without Language")
                .language(null)
                .coverUrl(null)
                .authorNames(List.of())
                .genreNames(List.of())
                .createdAt(bookWithNullLanguage.getCreatedAt())
                .build();

        Page<Book> bookPage = new PageImpl<>(List.of(bookWithNullLanguage), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toBookSummaryResponse(bookWithNullLanguage)).thenReturn(responseWithNullLanguage);

        Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getLanguage()).isNull();

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllBooks_WhenBookHasNullCoverUrl_ShouldReturnNullCoverUrl() {
        Book bookWithNullCover = Book.builder()
                .id(UUID.randomUUID())
                .title("Book Without Cover")
                .summary("Test summary")
                .language(BookLanguage.ENGLISH)
                .coverUrl(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .bookAuthors(List.of())
                .genres(Set.of())
                .build();

        BookSummaryResponse responseWithNullCover = BookSummaryResponse.builder()
                .id(bookWithNullCover.getId())
                .title("Book Without Cover")
                .language("ENGLISH")
                .coverUrl(null)
                .authorNames(List.of())
                .genreNames(List.of())
                .createdAt(bookWithNullCover.getCreatedAt())
                .build();

        Page<Book> bookPage = new PageImpl<>(List.of(bookWithNullCover), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toBookSummaryResponse(bookWithNullCover)).thenReturn(responseWithNullCover);

        Page<BookSummaryResponse> result = bookService.getAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getCoverUrl()).isNull();

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllBooks_ShouldUseCorrectPageableParameters() {
        Pageable customPageable = PageRequest.of(2, 15);
        Page<Book> emptyPage = new PageImpl<>(List.of(), customPageable, 0);
        when(bookRepository.findAll(customPageable)).thenReturn(emptyPage);

        bookService.getAllBooks(customPageable);

        verify(bookRepository, times(1)).findAll(customPageable);
    }

    @Test
    void getAllBooks_WhenRepositoryThrowsException_ShouldPropagateException() {
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(bookRepository.findAll(pageable)).thenThrow(exception);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            bookService.getAllBooks(pageable);
        });

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, never()).toBookSummaryResponse(any());
    }
}