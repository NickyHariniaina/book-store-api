package com.onlydevs.bookstore.model.dto;

import com.onlydevs.bookstore.model.Author;

public record CreateAuthorDto(
        String firstName,
        String lastName
) {
    public Author toAuthor() {
        return Author.builder()
                .lastName(lastName)
                .firstName(firstName)
                .bookAuthors(null)
                .build();
    }
}
