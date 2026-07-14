package com.onlydevs.bookstore.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ExternalApiConf {

  private final String openLibraryUrl;
  private final String googleBooksUrl;
  private final String googleBooksApiKey;

  public ExternalApiConf(
      @Value("${external.api.openlibrary.url}") String openLibraryUrl,
      @Value("${external.api.googlebooks.url}") String googleBooksUrl,
      @Value("${external.api.googlebooks.key}") String googleBooksApiKey) {
    this.openLibraryUrl = openLibraryUrl;
    this.googleBooksUrl = googleBooksUrl;
    this.googleBooksApiKey = googleBooksApiKey;
  }

  @Bean
  public RestTemplate externalRestTemplate() {
    return new RestTemplate();
  }

  public String getOpenLibraryUrl() {
    return openLibraryUrl;
  }

  public String getGoogleBooksUrl() {
    return googleBooksUrl;
  }

  public String getGoogleBooksApiKey() {
    return googleBooksApiKey;
  }
}
