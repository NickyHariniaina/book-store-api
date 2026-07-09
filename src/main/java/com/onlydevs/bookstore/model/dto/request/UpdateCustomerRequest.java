package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.Email;
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
public class UpdateCustomerRequest {
  @Size(max = 100)
  private String firstName;

  @Size(max = 100)
  private String lastName;

  @Email
  @Size(max = 255)
  private String email;

  @Size(max = 50)
  private String phone;
}
