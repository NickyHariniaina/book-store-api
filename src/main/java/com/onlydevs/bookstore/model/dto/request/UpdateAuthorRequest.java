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
public class UpdateAuthorRequest {
  @Size(max = 255)
  private String firstName;

  @Size(max = 255)
  private String lastName;
}
