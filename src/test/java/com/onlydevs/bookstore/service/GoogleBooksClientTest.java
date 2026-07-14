package com.onlydevs.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GoogleBooksClientTest {

  @Mock private RestTemplate restTemplate;

  private GoogleBooksClient client;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String apiUrl = "https://www.googleapis.com/books/v1";
  private final String apiKey = "test-key";
  private final String isbn = "9780385472579";
  private JsonNode foundResponse;
  private JsonNode notFoundResponse;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    client = new GoogleBooksClient(apiUrl, apiKey, restTemplate);

    foundResponse =
        mapper.readTree(
            """
            {
              "totalItems": 1,
              "items": [
                {
                  "volumeInfo": {
                    "title": "Things Fall Apart",
                    "authors": ["Chinua Achebe"],
                    "publisher": "Anchor",
                    "publishedDate": "1994",
                    "description": "A classic novel",
                    "pageCount": 209,
                    "categories": ["Fiction"],
                    "imageLinks": {
                      "smallThumbnail": "http://books.google.com/s.jpg",
                      "thumbnail": "http://books.google.com/m.jpg"
                    }
                  }
                }
              ]
            }
            """);

    notFoundResponse =
        mapper.readTree(
            """
            {
              "totalItems": 0,
              "items": []
            }
            """);
  }

  @Test
  void findByIsbn_WhenFound_ShouldReturnResponse() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class))).willReturn(foundResponse);

    var result = client.findByIsbn(isbn);

    assertThat(result).isPresent();
    var response = result.get();
    assertThat(response.getTitle()).isEqualTo("Things Fall Apart");
    assertThat(response.getAuthors()).hasSize(1);
    assertThat(response.getAuthors().getFirst().getName()).isEqualTo("Chinua Achebe");
    assertThat(response.getPublishers()).hasSize(1);
    assertThat(response.getPublishers().getFirst().getName()).isEqualTo("Anchor");
    assertThat(response.getPublishDate()).isEqualTo("1994");
    assertThat(response.getDescription()).isEqualTo("A classic novel");
    assertThat(response.getNumberOfPages()).isEqualTo(209);
    assertThat(response.getSubjects()).hasSize(1);
    assertThat(response.getSubjects().getFirst().getName()).isEqualTo("Fiction");
    assertThat(response.getCover()).isNotNull();
    assertThat(response.getCover().getSmall()).isEqualTo("http://books.google.com/s.jpg");
    assertThat(response.getCover().getMedium()).isEqualTo("http://books.google.com/m.jpg");
    assertThat(response.getIsbn()).isEqualTo(isbn);
  }

  @Test
  void findByIsbn_WhenNotFound_ShouldReturnEmpty() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class))).willReturn(notFoundResponse);

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIsbn_WhenApiThrows_ShouldReturnEmpty() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
        .willThrow(new RuntimeException("API error"));

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
  }
}
