package com.onlydevs.bookstore.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePublisherRequest {

  @Size(min = 1, max = 255)
  private String name;

  @Size(max = 500)
  @URL
  private String website;

  @Email
  @Size(max = 255)
  private String email;

  @Size(max = 20)
  private String phone;

  @Size(max = 100)
  private String country;
}
