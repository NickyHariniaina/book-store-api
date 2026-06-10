package com.onlydevs.bookstore.model.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
  private UUID id;
  private String title;
  private String summary;
  private String language;
  private String coverUrl;
  private List<BookAuthorResponse> authors;
  private List<GenreResponse> genres;
  private Instant createdAt;
  private Instant updatedAt;
}
