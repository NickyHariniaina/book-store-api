package com.onlydevs.bookstore.model.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExternalBookResponse {
  private final String title;
  private final String subtitle;
  private final List<AuthorEntry> authors;
  private final List<PublisherEntry> publishers;
  private final String publishDate;
  private final Integer numberOfPages;
  private final List<String> subjects;
  private final Cover cover;
  private final String description;
  private final String isbn;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class AuthorEntry {
    private final String name;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class PublisherEntry {
    private final String name;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class Cover {
    private final String small;
    private final String medium;
    private final String large;
  }
}
