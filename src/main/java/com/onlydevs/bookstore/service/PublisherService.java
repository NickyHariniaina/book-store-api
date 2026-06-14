package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.model.CreatePublisherRequest;
import com.onlydevs.bookstore.endpoint.rest.model.PublisherResponse;
import com.onlydevs.bookstore.endpoint.rest.model.UpdatePublisherRequest;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.model.mapper.PublisherMapper;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherService {

  private final PublisherRepository publisherRepository;
  private final PublisherMapper publisherMapper;

  public Page<PublisherResponse> getAllPublishers(Pageable pageable) {
    return publisherRepository.findAll(pageable).map(publisherMapper::toRest);
  }

  public PublisherResponse getPublisherById(UUID id) {
    var publisher =
        publisherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Publisher not found with id: " + id));
    return publisherMapper.toRest(publisher);
  }

  public PublisherResponse createPublisher(CreatePublisherRequest request) {
    if (request.getEmail() != null && publisherRepository.existsByEmailIgnoreCase(request.getEmail())) {
      throw new ConflictException("Publisher with email " + request.getEmail() + " already exists");
    }
    var publisher = publisherMapper.toDomain(request);
    return publisherMapper.toRest(publisherRepository.save(publisher));
  }

  public PublisherResponse updatePublisher(UUID id, UpdatePublisherRequest request) {
    var publisher =
        publisherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Publisher not found with id: " + id));
    if (request.getEmail() != null
        && !request.getEmail().equalsIgnoreCase(publisher.getEmail())
        && publisherRepository.existsByEmailIgnoreCase(request.getEmail())) {
      throw new ConflictException("Publisher with email " + request.getEmail() + " already exists");
    }
    publisherMapper.updateEntity(publisher, request);
    return publisherMapper.toRest(publisherRepository.save(publisher));
  }

  public void deletePublisher(UUID id) {
    var publisher =
        publisherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Publisher not found with id: " + id));
    publisherRepository.delete(publisher);
  }
}
