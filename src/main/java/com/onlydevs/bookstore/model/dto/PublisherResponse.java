package com.onlydevs.bookstore.model.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherResponse {
  public UUID id;
  public String name;
  public String website;
  public String email;
  public String phone;
  public String country;
  public Instant createdAt;
}
