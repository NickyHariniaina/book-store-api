package com.onlydevs.bookstore.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublisherRequest {
  @NotBlank(message = "Name is required")
  @Size(max = 200)
  public String name;

  @Size(max = 255)
  public String website;

  @Email
  @Size(max = 255)
  public String email;

  @Size(max = 50)
  public String phone;

  @Size(max = 100)
  public String country;
}
