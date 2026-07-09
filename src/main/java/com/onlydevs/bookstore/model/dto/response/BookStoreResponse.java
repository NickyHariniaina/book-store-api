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
public class BookStoreResponse {

  private UUID id;
  private String name;
  private String address;
  private String phone;
  private String email;
  private Instant createdAt;
  private Instant updatedAt;
}
