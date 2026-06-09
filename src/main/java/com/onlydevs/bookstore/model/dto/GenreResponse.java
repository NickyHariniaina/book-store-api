package com.onlydevs.bookstore.model.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GenreResponse {
  public UUID id;
  public String name;
  public String description;
}
