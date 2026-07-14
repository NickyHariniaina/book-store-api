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
public class OpenLibraryClient {

  private final RestTemplate externalRestTemplate;
  private final ExternalApiConf externalApiConf;

  public Optional<ExternalBookResponse> findByIsbn(String isbn) {
    try {
      var url =
          externalApiConf.getOpenLibraryUrl()
              + "/api/books?bibkeys=ISBN:"
              + isbn
              + "&format=json&jscmd=data";
      var root = externalRestTemplate.getForObject(url, JsonNode.class);

      if (root == null || root.isEmpty()) {
        return Optional.empty();
      }

      var bookKey = "ISBN:" + isbn;
      var bookNode = root.get(bookKey);
      if (bookNode == null) {
        return Optional.empty();
      }

      var details = bookNode.get("details");
      if (details == null) {
        return Optional.empty();
      }

      return Optional.of(toResponse(details, isbn, bookNode));
    } catch (Exception e) {
      log.warn("OpenLibrary lookup failed for ISBN {}: {}", isbn, e.getMessage());
      return Optional.empty();
    }
  }

  private ExternalBookResponse toResponse(JsonNode details, String isbn, JsonNode bookNode) {
    var title = details.has("title") ? details.get("title").asText() : null;

    var authors = new ArrayList<String>();
    var authorNodes = details.get("authors");
    if (authorNodes != null && authorNodes.isArray()) {
      for (var author : authorNodes) {
        var name = author.get("name");
        if (name != null) {
          authors.add(name.asText());
        }
      }
    }

    var publisher = extractFirstField(details, "publishers", "name");
    var publishedDate = details.has("publish_date") ? details.get("publish_date").asText() : null;
    var description =
        details.has("description")
            ? details.get("description").asText()
            : details.has("subtitle") ? details.get("subtitle").asText() : null;
    var pageCount = details.has("number_of_pages") ? details.get("number_of_pages").asInt() : null;

    var categories = new ArrayList<String>();
    var subjectNodes = details.get("subjects");
    if (subjectNodes != null && subjectNodes.isArray()) {
      for (var subject : subjectNodes) {
        var name = subject.get("name");
        if (name != null) {
          categories.add(name.asText());
        }
      }
    }

    var thumbnailUrl = extractThumbnail(details, bookNode);

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

  private String extractFirstField(JsonNode parent, String fieldName, String subField) {
    var field = parent.get(fieldName);
    if (field != null && field.isArray() && field.size() > 0) {
      var first = field.get(0);
      if (first != null && first.has(subField)) {
        return first.get(subField).asText();
      }
    }
    return null;
  }

  private String extractThumbnail(JsonNode details, JsonNode bookNode) {
    var cover = details.get("cover");
    if (cover != null) {
      if (cover.has("large")) {
        return cover.get("large").asText();
      }
      if (cover.has("medium")) {
        return cover.get("medium").asText();
      }
      if (cover.has("small")) {
        return cover.get("small").asText();
      }
    }
    if (bookNode.has("thumbnail_url")) {
      return bookNode.get("thumbnail_url").asText();
    }
    return null;
  }
}
