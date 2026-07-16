package com.onlydevs.bookstore.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class GoogleBooksClientTest {

  private MockRestServiceServer server;
  private GoogleBooksClient client;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String apiUrl = "https://www.googleapis.com/books/v1";
  private final String apiKey = "test-key";
  private final String isbn = "9780385472579";
  private JsonNode foundResponse;
  private JsonNode notFoundResponse;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    var builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    client = new GoogleBooksClient(apiUrl, apiKey, builder.build());

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
    var url = apiUrl + "/volumes?q=isbn:" + isbn + "&key=" + apiKey;

    server
        .expect(requestTo(url))
        .andRespond(withSuccess(foundResponse.toString(), MediaType.APPLICATION_JSON));

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

    server.verify();
  }

  @Test
  void findByIsbn_WhenNotFound_ShouldReturnEmpty() {
    var url = apiUrl + "/volumes?q=isbn:" + isbn + "&key=" + apiKey;

    server
        .expect(requestTo(url))
        .andRespond(withSuccess(notFoundResponse.toString(), MediaType.APPLICATION_JSON));

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }

  @Test
  void findByIsbn_WhenApiError_ShouldReturnEmpty() {
    var url = apiUrl + "/volumes?q=isbn:" + isbn + "&key=" + apiKey;

    server.expect(requestTo(url)).andRespond(withServerError());

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }

  @Test
  void findByIsbn_WhenClientError_ShouldReturnEmpty() {
    var url = apiUrl + "/volumes?q=isbn:" + isbn + "&key=" + apiKey;

    server.expect(requestTo(url)).andRespond(withBadRequest());

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }
}
