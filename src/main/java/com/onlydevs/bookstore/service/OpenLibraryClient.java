package com.onlydevs.bookstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlydevs.bookstore.model.dto.response.BookResponse;
import com.onlydevs.bookstore.model.dto.response.BookResponse.AuthorEntry;
import com.onlydevs.bookstore.model.dto.response.BookResponse.Cover;
import com.onlydevs.bookstore.model.dto.response.BookResponse.PublisherEntry;
import com.onlydevs.bookstore.model.dto.response.BookResponse.SubjectEntry;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class OpenLibraryClient {

  private final String apiUrl;
  private final RestTemplate restTemplate;

  public OpenLibraryClient(String apiUrl) {
    this(apiUrl, new RestTemplate());
  }

  OpenLibraryClient(String apiUrl, RestTemplate restTemplate) {
    this.apiUrl = apiUrl;
    this.restTemplate = restTemplate;
  }

  public Optional<BookResponse> findByIsbn(String isbn) {
    try {
      var url = apiUrl + "/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
      var root = restTemplate.getForObject(url, JsonNode.class);

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

      return Optional.of(toResponse(details, isbn));
    } catch (Exception e) {
      log.warn("OpenLibrary lookup failed for ISBN {}: {}", isbn, e.getMessage());
      return Optional.empty();
    }
  }

  private BookResponse toResponse(JsonNode details, String isbn) {
    var title = details.has("title") ? details.get("title").asText() : null;
    var subtitle = details.has("subtitle") ? details.get("subtitle").asText() : null;

    var authors = new ArrayList<AuthorEntry>();
    var authorNodes = details.get("authors");
    if (authorNodes != null && authorNodes.isArray()) {
      for (var author : authorNodes) {
        var name = author.get("name");
        if (name != null) {
          authors.add(AuthorEntry.builder().name(name.asText()).build());
        }
      }
    }

    var publishers = new ArrayList<PublisherEntry>();
    var publisherNodes = details.get("publishers");
    if (publisherNodes != null && publisherNodes.isArray()) {
      for (var pub : publisherNodes) {
        var name = pub.get("name");
        if (name != null) {
          publishers.add(PublisherEntry.builder().name(name.asText()).build());
        }
      }
    }

    var publishDate = details.has("publish_date") ? details.get("publish_date").asText() : null;

    var description =
        details.has("description")
            ? details.get("description").asText()
            : subtitle != null ? subtitle : null;

    var numberOfPages =
        details.has("number_of_pages") ? details.get("number_of_pages").asInt() : null;

    var subjects = new ArrayList<SubjectEntry>();
    var subjectNodes = details.get("subjects");
    if (subjectNodes != null && subjectNodes.isArray()) {
      for (var subject : subjectNodes) {
        var name = subject.get("name");
        if (name != null) {
          subjects.add(SubjectEntry.builder().name(name.asText()).build());
        }
      }
    }

    var cover = buildCover(details);

    return BookResponse.builder()
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

  private Cover buildCover(JsonNode details) {
    var coverNode = details.get("cover");
    if (coverNode == null) {
      return null;
    }
    var small = coverNode.has("small") ? coverNode.get("small").asText() : null;
    var medium = coverNode.has("medium") ? coverNode.get("medium").asText() : null;
    var large = coverNode.has("large") ? coverNode.get("large").asText() : null;
    if (small == null && medium == null && large == null) {
      return null;
    }
    return Cover.builder().small(small).medium(medium).large(large).build();
  }
}
