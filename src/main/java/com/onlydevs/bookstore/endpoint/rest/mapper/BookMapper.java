package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookAuthor;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {
  private final GenreMapper genreMapper;

  public BookSummaryResponse toBookSummaryResponse(Book book) {
    if (book == null) {
      return null;
    }
    return BookSummaryResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .language(book.getLanguage() != null ? book.getLanguage().name() : null)
        .coverUrl(book.getCoverUrl())
        .authorNames(extractAuthorNames(book))
        .genreNames(extractGenreNames(book))
        .createdAt(book.getCreatedAt())
        .build();
  }

  public BookDetailResponse toBookDetailResponse(Book book) {
    if (book == null) {
      return null;
    }
    return BookDetailResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .summary(book.getSummary())
        .language(book.getLanguage() != null ? book.getLanguage().name() : null)
        .coverUrl(book.getCoverUrl())
        .authors(toBookAuthorResponseList(book))
        .genres(toGenreResponseList(book))
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  public List<BookAuthorResponse> toBookAuthorResponseList(Book book) {
    if (book.getBookAuthors() == null || book.getBookAuthors().isEmpty()) {
      return List.of();
    }
    return book.getBookAuthors().stream()
        .sorted(
            Comparator.comparing(
                ba ->
                    ba.getContributionOrder() != null
                        ? ba.getContributionOrder()
                        : Integer.MAX_VALUE))
        .map(this::toBookAuthorResponse)
        .collect(Collectors.toList());
  }

  public BookAuthorResponse toBookAuthorResponse(BookAuthor bookAuthor) {
    return BookAuthorResponse.builder()
        .id(bookAuthor.getId())
        .bookId(bookAuthor.getBook().getId())
        .bookTitle(bookAuthor.getBook().getTitle())
        .authorId(bookAuthor.getAuthor().getId())
        .authorFullName(bookAuthor.getAuthor().getFullName())
        .role(bookAuthor.getRole())
        .contributionOrder(bookAuthor.getContributionOrder())
        .build();
  }

  public List<GenreResponse> toGenreResponseList(Book book) {
    if (book.getGenres() == null || book.getGenres().isEmpty()) {
      return List.of();
    }
    return book.getGenres().stream().map(genreMapper::toRest).collect(Collectors.toList());
  }

  private List<String> extractAuthorNames(Book book) {
    if (book.getBookAuthors() == null || book.getBookAuthors().isEmpty()) {
      return List.of();
    }

    return book.getBookAuthors().stream()
        .sorted(
            Comparator.comparing(
                ba ->
                    ba.getContributionOrder() != null
                        ? ba.getContributionOrder()
                        : Integer.MAX_VALUE))
        .map(ba -> ba.getAuthor() != null ? ba.getAuthor().getFullName() : null)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<String> extractGenreNames(Book book) {
    if (book.getGenres() == null || book.getGenres().isEmpty()) {
      return List.of();
    }

    return book.getGenres().stream()
        .map(Genre::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
