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
public class AuthorResponse {
  private UUID id;
  private String firstName;
  private String lastName;
  private String fullName;
  private Instant createdAt;
}
