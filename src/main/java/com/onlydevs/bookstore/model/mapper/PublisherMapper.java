package com.onlydevs.bookstore.model.mapper;

import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.PublisherResponse;
import com.onlydevs.bookstore.model.dto.UpdatePublisherRequest;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

  public PublisherResponse toResponse(Publisher publisher) {
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

  public Publisher toEntity(CreatePublisherRequest request) {
    return Publisher.builder()
        .name(request.name)
        .website(request.website)
        .email(request.email)
        .phone(request.phone)
        .country(request.country)
        .build();
  }

  public Publisher updateEntity(Publisher publisher, UpdatePublisherRequest request) {
    if (request.name != null) {
      publisher.setName(request.name);
    }
    if (request.website != null) {
      publisher.setWebsite(request.website);
    }
    if (request.email != null) {
      publisher.setEmail(request.email);
    }
    if (request.phone != null) {
      publisher.setPhone(request.phone);
    }
    if (request.country != null) {
      publisher.setCountry(request.country);
    }
    return publisher;
  }
}
