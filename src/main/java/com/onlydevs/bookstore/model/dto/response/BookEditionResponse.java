package com.onlydevs.bookstore.model.dto.response;

import java.time.Instant;
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
public class BookEditionResponse {
  private UUID id;
  private UUID bookId;
  private String bookTitle;
  private PublisherResponse publisher;
  private String isbn;
  private String edition;
  private String format;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
