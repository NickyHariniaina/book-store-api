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
  private List<String> authors;
  private String publisher;
  private String publishedDate;
  private String description;
  private Integer pageCount;
  private List<String> categories;
  private String thumbnailUrl;
  private String isbn;
}
