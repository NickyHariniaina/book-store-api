package com.onlydevs.bookstore.model.dto.response;

import java.util.List;
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
public class ExternalBookResponse {
  private String title;
  private String subtitle;
  private List<AuthorEntry> authors;
  private List<PublisherEntry> publishers;
  private String publishDate;
  private Integer numberOfPages;
  private List<SubjectEntry> subjects;
  private Cover cover;
  private String description;
  private String isbn;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AuthorEntry {
    private String name;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PublisherEntry {
    private String name;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SubjectEntry {
    private String name;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Cover {
    private String small;
    private String medium;
    private String large;
  }
}
