package com.onlydevs.bookstore.service.external;

import com.onlydevs.bookstore.model.dto.response.ExternalBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBookService {

  private final OpenLibraryClient openLibraryClient;
  private final GoogleBooksClient googleBooksClient;

  public ExternalBookResponse findByIsbn(String isbn) {
    var result = openLibraryClient.findByIsbn(isbn);
    if (result.isPresent()) {
      log.info("Book found in OpenLibrary for ISBN {}", isbn);
      return result.get();
    }

    log.info("Book not found in OpenLibrary for ISBN {}, trying Google Books", isbn);
    result = googleBooksClient.findByIsbn(isbn);
    if (result.isPresent()) {
      log.info("Book found in Google Books for ISBN {}", isbn);
      return result.get();
    }

    return null;
  }
}
