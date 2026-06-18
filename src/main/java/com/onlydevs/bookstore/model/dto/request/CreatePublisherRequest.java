package com.onlydevs.bookstore.model.dto.request;

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
public class CreatePublisherRequest {
  private String name;
  private String website;
  private String email;
  private String phone;
  private String country;
}
