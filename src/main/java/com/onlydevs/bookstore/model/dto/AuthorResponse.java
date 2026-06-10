package com.onlydevs.bookstore.model.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthorResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String fullName;
  private Instant createdAt;
}
