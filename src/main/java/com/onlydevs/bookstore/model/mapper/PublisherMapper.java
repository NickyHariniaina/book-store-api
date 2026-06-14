package com.onlydevs.bookstore.model.mapper;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.Publisher;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

  public PublisherResponse toRest(Publisher publisher) {
    return new PublisherResponse()
        .id(publisher.getId())
        .name(publisher.getName())
        .website(publisher.getWebsite())
        .email(publisher.getEmail())
        .phone(publisher.getPhone())
        .country(publisher.getCountry())
        .createdAt(publisher.getCreatedAt());
  }

  public Publisher toDomain(CreatePublisherRequest request) {
    return Publisher.builder()
        .name(request.getName())
        .website(request.getWebsite())
        .email(request.getEmail())
        .phone(request.getPhone())
        .country(request.getCountry())
        .build();
  }

  public Publisher updateEntity(Publisher publisher, UpdatePublisherRequest request) {
    if (request.getName() != null) {
      publisher.setName(request.getName());
    }
    if (request.getWebsite() != null) {
      publisher.setWebsite(request.getWebsite());
    }
    if (request.getEmail() != null) {
      publisher.setEmail(request.getEmail());
    }
    if (request.getPhone() != null) {
      publisher.setPhone(request.getPhone());
    }
    if (request.getCountry() != null) {
      publisher.setCountry(request.getCountry());
    }
    return publisher;
  }
}
