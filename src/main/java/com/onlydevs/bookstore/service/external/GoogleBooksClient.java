package com.onlydevs.bookstore.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleBooksClient {

  private final RestTemplate externalRestTemplate;
  private final ExternalApiConf externalApiConf;

  public Optional<ExternalBookResponse> findByIsbn(String isbn) {
    try {
      var url =
          externalApiConf.getGoogleBooksUrl()
              + "/volumes?q=isbn:"
              + isbn
              + "&key="
              + externalApiConf.getGoogleBooksApiKey();
      var root = externalRestTemplate.getForObject(url, JsonNode.class);

      if (root == null) {
        return Optional.empty();
      }

      var totalItems = root.has("totalItems") ? root.get("totalItems").asInt() : 0;
      if (totalItems == 0) {
        return Optional.empty();
      }

      var items = root.get("items");
      if (items == null || !items.isArray() || items.isEmpty()) {
        return Optional.empty();
      }

      var volumeInfo = items.get(0).get("volumeInfo");
      if (volumeInfo == null) {
        return Optional.empty();
      }

      return Optional.of(toResponse(volumeInfo, isbn));
    } catch (Exception e) {
      log.warn("Google Books lookup failed for ISBN {}: {}", isbn, e.getMessage());
      return Optional.empty();
    }
  }

  private ExternalBookResponse toResponse(JsonNode volumeInfo, String isbn) {
    var title = volumeInfo.has("title") ? volumeInfo.get("title").asText() : null;

    var authors = new ArrayList<String>();
    var authorNodes = volumeInfo.get("authors");
    if (authorNodes != null && authorNodes.isArray()) {
      for (var author : authorNodes) {
        authors.add(author.asText());
      }
    }

    var publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").asText() : null;
    var publishedDate =
        volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").asText() : null;
    var description = volumeInfo.has("description") ? volumeInfo.get("description").asText() : null;
    var pageCount = volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").asInt() : null;

    var categories = new ArrayList<String>();
    var categoryNodes = volumeInfo.get("categories");
    if (categoryNodes != null && categoryNodes.isArray()) {
      for (var category : categoryNodes) {
        categories.add(category.asText());
      }
    }

    var thumbnailUrl = extractThumbnail(volumeInfo);

    return ExternalBookResponse.builder()
        .title(title)
        .authors(authors.isEmpty() ? null : authors)
        .publisher(publisher)
        .publishedDate(publishedDate)
        .description(description)
        .pageCount(pageCount)
        .categories(categories.isEmpty() ? null : categories)
        .thumbnailUrl(thumbnailUrl)
        .isbn(isbn)
        .build();
  }

  private String extractThumbnail(JsonNode volumeInfo) {
    var imageLinks = volumeInfo.get("imageLinks");
    if (imageLinks != null) {
      if (imageLinks.has("thumbnail")) {
        return imageLinks.get("thumbnail").asText();
      }
      if (imageLinks.has("smallThumbnail")) {
        return imageLinks.get("smallThumbnail").asText();
      }
    }
    return null;
  }
}
