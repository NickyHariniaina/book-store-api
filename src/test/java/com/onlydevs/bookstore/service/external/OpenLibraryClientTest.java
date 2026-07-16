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
class OpenLibraryClientTest {

  private MockRestServiceServer server;
  private OpenLibraryClient client;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String apiUrl = "https://openlibrary.org";
  private final String isbn = "9780385472579";
  private JsonNode foundResponse;
  private JsonNode emptyResponse;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    var builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    client = new OpenLibraryClient(apiUrl, builder.build());

    foundResponse =
        mapper.readTree(
            """
{
  "ISBN:9780385472579": {
    "title": "Things Fall Apart",
    "authors": [{"name": "Chinua Achebe"}],
    "publishers": [{"name": "Anchor"}],
    "publish_date": "1994",
    "number_of_pages": 209,
    "subjects": [{"name": "Fiction"}],
    "cover": {"small": "http://covers.org/s.jpg", "medium": "http://covers.org/m.jpg", "large": "http://covers.org/l.jpg"}
  }
}
""");

    emptyResponse = mapper.readTree("{}");
  }

  @Test
  void findByIsbn_WhenFound_ShouldReturnResponse() {
    var url = apiUrl + "/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

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
    assertThat(response.getNumberOfPages()).isEqualTo(209);
    assertThat(response.getSubjects()).hasSize(1);
    assertThat(response.getSubjects().getFirst()).isEqualTo("Fiction");
    assertThat(response.getCover()).isNotNull();
    assertThat(response.getCover().getSmall()).isEqualTo("http://covers.org/s.jpg");
    assertThat(response.getCover().getMedium()).isEqualTo("http://covers.org/m.jpg");
    assertThat(response.getCover().getLarge()).isEqualTo("http://covers.org/l.jpg");
    assertThat(response.getIsbn()).isEqualTo(isbn);

    server.verify();
  }

  @Test
  void findByIsbn_WhenNotFound_ShouldReturnEmpty() {
    var url = apiUrl + "/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

    server
        .expect(requestTo(url))
        .andRespond(withSuccess(emptyResponse.toString(), MediaType.APPLICATION_JSON));

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }

  @Test
  void findByIsbn_WhenApiError_ShouldReturnEmpty() {
    var url = apiUrl + "/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

    server.expect(requestTo(url)).andRespond(withServerError());

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }

  @Test
  void findByIsbn_WhenClientError_ShouldReturnEmpty() {
    var url = apiUrl + "/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

    server.expect(requestTo(url)).andRespond(withBadRequest());

    var result = client.findByIsbn(isbn);

    assertThat(result).isEmpty();
    server.verify();
  }
}
