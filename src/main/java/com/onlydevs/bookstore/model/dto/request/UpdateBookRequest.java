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
public class UpdateBookRequest {
  @Size(max = 255)
  private String title;

  @Size(max = 2000)
  private String summary;

  @Size(max = 50)
  private String language;

  @Size(max = 500)
  private String coverUrl;
}
