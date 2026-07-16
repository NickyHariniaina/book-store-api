package com.onlydevs.bookstore.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.AuthorEntry;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.Cover;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.PublisherEntry;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.SubjectEntry;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public class GoogleBooksClient {

  private final String apiUrl;
  private final String apiKey;
  private final RestClient restClient;

  public GoogleBooksClient(String apiUrl, String apiKey) {
    this(apiUrl, apiKey, RestClient.create());
  }

  GoogleBooksClient(String apiUrl, String apiKey, RestClient restClient) {
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
    this.restClient = restClient;
  }

  public Optional<ExternalBookResponse> findByIsbn(String isbn) {
    try {
      var url = apiUrl + "/volumes?q=isbn:" + isbn + "&key=" + apiKey;
      var root = restClient.get().uri(url).retrieve().body(JsonNode.class);

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
    var subtitle = volumeInfo.has("subtitle") ? volumeInfo.get("subtitle").asText() : null;

    var authors = new ArrayList<AuthorEntry>();
    var authorNodes = volumeInfo.get("authors");
    if (authorNodes != null && authorNodes.isArray()) {
      for (var author : authorNodes) {
        authors.add(AuthorEntry.builder().name(author.asText()).build());
      }
    }

    var publishers = new ArrayList<PublisherEntry>();
    if (volumeInfo.has("publisher")) {
      publishers.add(PublisherEntry.builder().name(volumeInfo.get("publisher").asText()).build());
    }

    var publishDate =
        volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").asText() : null;

    var description =
        volumeInfo.has("description") ? volumeInfo.get("description").asText() : subtitle;

    var numberOfPages = volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").asInt() : null;

    var subjects = new ArrayList<SubjectEntry>();
    var categoryNodes = volumeInfo.get("categories");
    if (categoryNodes != null && categoryNodes.isArray()) {
      for (var category : categoryNodes) {
        subjects.add(SubjectEntry.builder().name(category.asText()).build());
      }
    }

    var cover = buildCover(volumeInfo);

    return ExternalBookResponse.builder()
        .title(title)
        .subtitle(subtitle)
        .authors(authors.isEmpty() ? null : authors)
        .publishers(publishers.isEmpty() ? null : publishers)
        .publishDate(publishDate)
        .description(description)
        .numberOfPages(numberOfPages)
        .subjects(subjects.isEmpty() ? null : subjects)
        .cover(cover)
        .isbn(isbn)
        .build();
  }

  private Cover buildCover(JsonNode volumeInfo) {
    var imageLinks = volumeInfo.get("imageLinks");
    if (imageLinks == null) {
      return null;
    }
    var small = imageLinks.has("smallThumbnail") ? imageLinks.get("smallThumbnail").asText() : null;
    var medium = imageLinks.has("thumbnail") ? imageLinks.get("thumbnail").asText() : null;
    var large = imageLinks.has("large") ? imageLinks.get("large").asText() : null;
    if (small == null && medium == null && large == null) {
      return null;
    }
    return Cover.builder().small(small).medium(medium).large(large).build();
  }
}
