package com.onlydevs.bookstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookClientConf {

  private final String openLibraryUrl;
  private final String googleBooksUrl;
  private final String googleBooksApiKey;

  public BookClientConf(
      @Value("${api.openlibrary.url}") String openLibraryUrl,
      @Value("${api.googlebooks.url}") String googleBooksUrl,
      @Value("${api.googlebooks.key}") String googleBooksApiKey) {
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
