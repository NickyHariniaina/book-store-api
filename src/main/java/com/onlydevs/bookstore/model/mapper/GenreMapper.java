package com.onlydevs.bookstore.model.mapper;

import com.onlydevs.bookstore.endpoint.rest.model.BookSummaryResponse;
import com.onlydevs.bookstore.endpoint.rest.model.CreateGenreRequest;
import com.onlydevs.bookstore.endpoint.rest.model.GenreResponse;
import com.onlydevs.bookstore.endpoint.rest.model.RevenuePerGenreResponse;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

  public GenreResponse toRest(Genre genre) {
    return new GenreResponse()
        .id(genre.getId())
        .name(genre.getName())
        .description(genre.getDescription());
  }

  public Genre toDomain(CreateGenreRequest request) {
    return Genre.builder().name(request.getName()).description(request.getDescription()).build();
  }

  public BookSummaryResponse toBookSummaryResponse(Book book) {
    return new BookSummaryResponse()
        .id(book.getId())
        .title(book.getTitle())
        .language(book.getLanguage() != null ? book.getLanguage().name() : null)
        .coverUrl(book.getCoverUrl())
        .authorNames(
            book.getBookAuthors().stream()
                .map(ba -> ba.getAuthor().getFirstName() + " " + ba.getAuthor().getLastName())
                .toList())
        .genreNames(book.getGenres().stream().map(Genre::getName).toList())
        .createdAt(book.getCreatedAt());
  }

  public RevenuePerGenreResponse toRevenuePerGenreResponse(Object[] row) {
    return new RevenuePerGenreResponse()
        .genreName((String) row[0])
        .revenue(row[1] != null ? BigDecimal.valueOf(((Number) row[1]).doubleValue()) : BigDecimal.ZERO);
  }
}
