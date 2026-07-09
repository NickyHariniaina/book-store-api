package com.onlydevs.bookstore.model.dto.response;

import com.onlydevs.bookstore.model.enums.AuthorRole;
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
public class BookAuthorResponse {
  private UUID id;
  private UUID authorId;
  private String authorFullName;
  private AuthorRole role;
  private Integer contributionOrder;
}
