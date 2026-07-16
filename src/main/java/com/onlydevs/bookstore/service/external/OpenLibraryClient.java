package com.onlydevs.bookstore.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.AuthorEntry;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.Cover;
import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse.PublisherEntry;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class OpenLibraryClient {

  private final String apiUrl;
  private final RestClient restClient;

  public OpenLibraryClient(String apiUrl) {
    this(apiUrl, buildRestClient());
  }

  OpenLibraryClient(String apiUrl, RestClient restClient) {
    this.apiUrl = apiUrl;
    this.restClient = restClient;
  }

  private static RestClient buildRestClient() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    return RestClient.builder()
        .requestFactory(factory)
        .defaultHeader("User-Agent", "BookStoreAPI/1.0")
        .build();
  }

  public Optional<ExternalBookResponse> findByIsbn(String isbn) {
    try {
      var uri = buildUri(isbn);
      var root = restClient.get().uri(uri).retrieve().body(JsonNode.class);

      if (root == null || root.isEmpty()) {
        return Optional.empty();
      }

      var bookKey = "ISBN:" + isbn;
      var bookNode = root.get(bookKey);
      if (bookNode == null) {
        return Optional.empty();
      }

      return Optional.of(toResponse(bookNode, isbn));
    } catch (RestClientResponseException e) {
      log.warn(
          "OpenLibrary request failed for ISBN {}: {} - {}",
          isbn,
          e.getStatusCode(),
          e.getMessage());
      return Optional.empty();
    } catch (ResourceAccessException e) {
      log.warn("OpenLibrary connection failed for ISBN {}: {}", isbn, e.getMessage());
      return Optional.empty();
    }
  }

  private URI buildUri(String isbn) {
    return UriComponentsBuilder.fromHttpUrl(apiUrl)
        .path("/api/books")
        .queryParam("bibkeys", "ISBN:" + isbn)
        .queryParam("format", "json")
        .queryParam("jscmd", "data")
        .build()
        .toUri();
  }

  private ExternalBookResponse toResponse(JsonNode details, String isbn) {
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

    var description = details.has("description") ? details.get("description").asText() : null;

    var numberOfPages =
        details.has("number_of_pages") ? details.get("number_of_pages").asInt() : null;

    var subjects = new ArrayList<String>();
    var subjectNodes = details.get("subjects");
    if (subjectNodes != null && subjectNodes.isArray()) {
      for (var subject : subjectNodes) {
        var name = subject.get("name");
        if (name != null) {
          subjects.add(name.asText());
        }
      }
    }

    var cover = buildCover(details);

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
