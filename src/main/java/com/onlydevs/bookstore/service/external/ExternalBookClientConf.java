package com.onlydevs.bookstore.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalBookClientConf {

  private final String openLibraryUrl;
  private final String googleBooksUrl;
  private final String googleBooksApiKey;

  public ExternalBookClientConf(
      @Value("${external.api.openlibrary.url}") String openLibraryUrl,
      @Value("${external.api.googlebooks.url}") String googleBooksUrl,
      @Value("${external.api.googlebooks.key}") String googleBooksApiKey) {
    this.openLibraryUrl = openLibraryUrl;
    this.googleBooksUrl = googleBooksUrl;
    this.googleBooksApiKey = googleBooksApiKey;
  }

  @Bean
  public OpenLibraryClient openLibraryClient() {
    return new OpenLibraryClient(openLibraryUrl);
  }

  @Bean
  public GoogleBooksClient googleBooksClient() {
    return new GoogleBooksClient(googleBooksUrl, googleBooksApiKey);
  }
}
