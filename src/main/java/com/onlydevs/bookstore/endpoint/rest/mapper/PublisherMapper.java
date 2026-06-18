package com.onlydevs.bookstore.endpoint.rest.mapper;

import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.request.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.request.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.dto.response.PublisherResponse;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

  public PublisherResponse toRest(Publisher publisher) {
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
