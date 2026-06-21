package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ArrivalRequest {

  @NotNull private UUID editionId;

  @NotNull @Positive private Integer quantity;

  @Size(max = 255)
  private String reference;
}
