package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateBookEditionRequest {
  @NotNull private UUID publisherId;

  @NotBlank
  @Size(min = 10, max = 13)
  private String isbn;

  @Size(max = 50)
  private String edition;

  @NotBlank private String format;
}
