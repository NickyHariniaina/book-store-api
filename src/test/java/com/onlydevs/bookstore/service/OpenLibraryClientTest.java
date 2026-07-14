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
class OpenLibraryClientTest {

  @Mock private RestTemplate restTemplate;

  private OpenLibraryClient client;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String apiUrl = "https://openlibrary.org";
  private final String isbn = "9780385472579";
  private JsonNode foundResponse;
  private JsonNode emptyResponse;
  private JsonNode noDetailsResponse;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    client = new OpenLibraryClient(apiUrl, restTemplate);

    foundResponse =
        mapper.readTree(
            """
{
  "ISBN:9780385472579": {
    "bib_key": "ISBN:9780385472579",
    "details": {
      "title": "Things Fall Apart",
      "authors": [{"name": "Chinua Achebe"}],
      "publishers": [{"name": "Anchor"}],
      "publish_date": "1994",
      "number_of_pages": 209,
      "subjects": [{"name": "Fiction"}],
      "cover": {"small": "http://covers.org/s.jpg", "medium": "http://covers.org/m.jpg", "large": "http://covers.org/l.jpg"}
    }
  }
}
""");

    emptyResponse = mapper.readTree("{}");

    noDetailsResponse =
        mapper.readTree(
            """
            {
              "ISBN:9780385472579": {
                "bib_key": "ISBN:9780385472579"
              }
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
    assertThat(response.getNumberOfPages()).isEqualTo(209);
    assertThat(response.getSubjects()).hasSize(1);
    assertThat(response.getSubjects().getFirst().getName()).isEqualTo("Fiction");
    assertThat(response.getCover()).isNotNull();
    assertThat(response.getCover().getSmall()).isEqualTo("http://covers.org/s.jpg");
    assertThat(response.getCover().getMedium()).isEqualTo("http://covers.org/m.jpg");
    assertThat(response.getCover().getLarge()).isEqualTo("http://covers.org/l.jpg");
    assertThat(response.getIsbn()).isEqualTo(isbn);
  }

  @Test
  void findByIsbn_WhenNotFound_ShouldReturnEmpty() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class))).willReturn(emptyResponse);

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIsbn_WhenNoDetails_ShouldReturnEmpty() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class))).willReturn(noDetailsResponse);

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIsbn_WhenApiThrows_ShouldReturnEmpty() {
    given(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
        .willThrow(new RuntimeException("Connection error"));

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
  }
}
