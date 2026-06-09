package com.onlydevs.bookstore.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryResponse {
  public UUID id;
  public String title;
  public String language;
  public String coverUrl;
  public List<String> authorNames;
  public List<String> genreNames;
  public Instant createdAt;
}
