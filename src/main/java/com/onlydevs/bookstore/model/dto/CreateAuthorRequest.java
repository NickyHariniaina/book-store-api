package com.onlydevs.bookstore.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorRequest(@NotBlank String firstName, @NotBlank String lastName) {}
