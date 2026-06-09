package com.onlydevs.bookstore.model.mapper;

import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.dto.CreateGenreRequest;
import com.onlydevs.bookstore.model.dto.GenreResponse;
import com.onlydevs.bookstore.model.dto.RevenuePerGenreResponse;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

  public GenreResponse toResponse(Genre genre) {
    return GenreResponse.builder()
        .id(genre.getId())
        .name(genre.getName())
        .description(genre.getDescription())
        .build();
  }

  public Genre toEntity(CreateGenreRequest request) {
    return Genre.builder().name(request.name).description(request.description).build();
  }

  public RevenuePerGenreResponse toRevenuePerGenreResponse(Object[] row) {
    return RevenuePerGenreResponse.builder()
        .genreName((String) row[0])
        .revenue(row[1] != null ? ((Number) row[1]).doubleValue() : 0.0)
        .build();
  }
}
