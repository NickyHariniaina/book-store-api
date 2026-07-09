package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.request.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.dto.response.GenreResponse;
import com.onlydevs.bookstore.model.dto.response.RevenuePerGenreResponse;
import com.onlydevs.bookstore.repository.GenreRevenue;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

  public GenreResponse toRest(Genre genre) {
    return GenreResponse.builder()
        .id(genre.getId())
        .name(genre.getName())
        .description(genre.getDescription())
        .build();
  }

  public Genre toDomain(CreateGenreRequest request) {
    return Genre.builder().name(request.getName()).description(request.getDescription()).build();
  }

  public BookSummaryResponse toBookSummaryResponse(Book book) {
    return BookSummaryResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .language(book.getLanguage() != null ? book.getLanguage().name() : null)
        .coverUrl(book.getCoverUrl())
        .authorNames(
            book.getBookAuthors().stream()
                .map(ba -> ba.getAuthor().getFirstName() + " " + ba.getAuthor().getLastName())
                .toList())
        .genreNames(book.getGenres().stream().map(Genre::getName).toList())
        .createdAt(book.getCreatedAt())
        .build();
  }

  public RevenuePerGenreResponse toRevenuePerGenreResponse(GenreRevenue row) {
    return RevenuePerGenreResponse.builder()
        .genreName(row.getName())
        .revenue(row.getRevenue() != null ? row.getRevenue() : BigDecimal.ZERO)
        .build();
  }
}
