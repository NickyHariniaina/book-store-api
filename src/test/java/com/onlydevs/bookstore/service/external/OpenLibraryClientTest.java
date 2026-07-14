package com.onlydevs.bookstore.service.external;

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
    assertThat(result.get().getTitle()).isEqualTo("Things Fall Apart");
    assertThat(result.get().getAuthors()).hasSize(1);
    assertThat(result.get().getAuthors().getFirst().getName()).isEqualTo("Chinua Achebe");
    assertThat(result.get().getPublishers()).hasSize(1);
    assertThat(result.get().getPublishers().getFirst().getName()).isEqualTo("Anchor");
    assertThat(result.get().getPublishDate()).isEqualTo("1994");
    assertThat(result.get().getNumberOfPages()).isEqualTo(209);
    assertThat(result.get().getSubjects()).hasSize(1);
    assertThat(result.get().getSubjects().getFirst().getName()).isEqualTo("Fiction");
    assertThat(result.get().getCover()).isNotNull();
    assertThat(result.get().getCover().getSmall()).isEqualTo("http://covers.org/s.jpg");
    assertThat(result.get().getCover().getMedium()).isEqualTo("http://covers.org/m.jpg");
    assertThat(result.get().getCover().getLarge()).isEqualTo("http://covers.org/l.jpg");
    assertThat(result.get().getIsbn()).isEqualTo(isbn);
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
