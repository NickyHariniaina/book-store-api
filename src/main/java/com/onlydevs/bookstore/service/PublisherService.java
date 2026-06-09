package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.CreatePublisherRequest;
import com.onlydevs.bookstore.model.dto.PublisherResponse;
import com.onlydevs.bookstore.model.dto.UpdatePublisherRequest;
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
    return publisherRepository.findAll(pageable).map(publisherMapper::toResponse);
  }

  public PublisherResponse getPublisherById(UUID id) {
    Publisher publisher =
        publisherRepository.findById(id).orElseThrow(() -> new NotFoundException("Publisher", id));
    return publisherMapper.toResponse(publisher);
  }

  public PublisherResponse createPublisher(CreatePublisherRequest request) {
    Publisher publisher = publisherMapper.toEntity(request);
    return publisherMapper.toResponse(publisherRepository.save(publisher));
  }

  public PublisherResponse updatePublisher(UUID id, UpdatePublisherRequest request) {
    Publisher publisher =
        publisherRepository.findById(id).orElseThrow(() -> new NotFoundException("Publisher", id));
    publisherMapper.updateEntity(publisher, request);
    return publisherMapper.toResponse(publisherRepository.save(publisher));
  }

  public void deletePublisher(UUID id) {
    Publisher publisher =
        publisherRepository.findById(id).orElseThrow(() -> new NotFoundException("Publisher", id));
    publisherRepository.delete(publisher);
  }
}
