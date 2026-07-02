package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookEditionMapper {
  public BookEditionResponse toBookEditionResponse(BookEdition edition) {
    if (edition == null) {
      return null;
    }
    return BookEditionResponse.builder()
        .id(edition.getId())
        .bookId(edition.getBook().getId())
        .bookTitle(edition.getBook().getTitle())
        .publisher(toPublisherResponse(edition.getPublisher()))
        .isbn(edition.getIsbn())
        .edition(edition.getEdition())
        .format(edition.getFormat().name())
        .active(edition.getActive())
        .createdAt(edition.getCreatedAt())
        .updatedAt(edition.getUpdatedAt())
        .build();
  }

  public List<BookEditionResponse> toBookEditionResponseList(List<BookEdition> editions) {
    return editions.stream().map(this::toBookEditionResponse).toList();
  }

  private PublisherResponse toPublisherResponse(Publisher publisher) {
    if (publisher == null) {
      return null;
    }
    return PublisherResponse.builder()
        .id(publisher.getId())
        .name(publisher.getName())
        .website(publisher.getWebsite())
        .email(publisher.getEmail())
        .phone(publisher.getPhone())
        .country(publisher.getCountry())
        .createdAt(publisher.getCreatedAt())
        .build();
  }
}
