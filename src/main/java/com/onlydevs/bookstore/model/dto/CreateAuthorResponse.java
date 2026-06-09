package com.onlydevs.bookstore.model.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public class CreateAuthorResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String fullName;
  private Instant createdAt;
}
