package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.Size;
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
public class UpdateBookEditionRequest {
  @Size(min = 10, max = 13)
  private String isbn;

  @Size(max = 50)
  private String edition;

  private String format;
}
