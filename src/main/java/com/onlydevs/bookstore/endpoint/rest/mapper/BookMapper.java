package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookMapper {
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

    private List<String> extractAuthorNames(Book book) {
        if (book.getBookAuthors() == null || book.getBookAuthors().isEmpty()) {
            return List.of();
        }

        return book.getBookAuthors().stream()
                .sorted(Comparator.comparing(
                        ba -> ba.getContributionOrder() != null ? ba.getContributionOrder() : Integer.MAX_VALUE
                ))
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