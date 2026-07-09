package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreateBookStoreRequest {

  @NotBlank
  @Size(max = 255)
  private String name;

  @Size(max = 500)
  private String address;

  @Size(max = 20)
  private String phone;

  @Email
  @Size(max = 255)
  private String email;
}
