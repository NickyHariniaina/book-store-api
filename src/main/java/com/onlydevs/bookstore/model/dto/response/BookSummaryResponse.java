package com.onlydevs.bookstore.model.dto.response;


import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryResponse {
    private UUID id;
    private String title;
    private String language;
    private String coverUrl;
    private List<String> authorNames;
    private List<String> genreNames;
    private Instant createdAt;
}