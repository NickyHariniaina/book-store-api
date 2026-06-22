package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookEditionMapper;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookEdition;
import com.onlydevs.bookstore.model.Publisher;
import com.onlydevs.bookstore.model.dto.request.CreateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookEditionRequest;
import com.onlydevs.bookstore.model.dto.response.BookEditionResponse;
import com.onlydevs.bookstore.model.enums.BookFormat;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.ConflictException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.PublisherRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BookEditionService {
  private final BookEditionRepository bookEditionRepository;
  private final BookRepository bookRepository;
  private final PublisherRepository publisherRepository;
  private final BookEditionMapper bookEditionMapper;

  public List<BookEditionResponse> getEditionsByBookId(UUID bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book not found with id: " + bookId);
    }
    return bookEditionMapper.toBookEditionResponseList(bookEditionRepository.findByBookId(bookId));
  }

  public BookEditionResponse getEditionById(UUID id) {
    BookEdition edition =
        bookEditionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Edition not found with id: " + id));
    return bookEditionMapper.toBookEditionResponse(edition);
  }

  public BookEditionResponse getEditionByIsbn(String isbn) {
    BookEdition edition =
        bookEditionRepository
            .findByIsbn(isbn)
            .orElseThrow(() -> new NotFoundException("Edition not found with isbn: " + isbn));
    return bookEditionMapper.toBookEditionResponse(edition);
  }

  @Transactional
  public BookEditionResponse createEdition(UUID bookId, CreateBookEditionRequest request) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));

    Publisher publisher =
        publisherRepository
            .findById(request.getPublisherId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Publisher not found with id: " + request.getPublisherId()));

    if (bookEditionRepository.existsByIsbn(request.getIsbn())) {
      throw new ConflictException("ISBN already exists: " + request.getIsbn());
    }

    BookEdition edition =
        BookEdition.builder()
            .isbn(request.getIsbn())
            .edition(request.getEdition())
            .format(parseFormat(request.getFormat()))
            .book(book)
            .publisher(publisher)
            .build();

    BookEdition saved = bookEditionRepository.save(edition);
    return bookEditionMapper.toBookEditionResponse(saved);
  }

  @Transactional
  public BookEditionResponse updateEdition(UUID id, UpdateBookEditionRequest request) {
    BookEdition edition =
        bookEditionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Edition not found with id: " + id));

    if (request.getIsbn() != null) {
      if (!request.getIsbn().equals(edition.getIsbn())
          && bookEditionRepository.existsByIsbn(request.getIsbn())) {
        throw new ConflictException("ISBN already exists: " + request.getIsbn());
      }
      edition.setIsbn(request.getIsbn());
    }
    if (request.getEdition() != null) {
      edition.setEdition(request.getEdition());
    }
    if (request.getFormat() != null) {
      edition.setFormat(parseFormat(request.getFormat()));
    }

    BookEdition saved = bookEditionRepository.save(edition);
    return bookEditionMapper.toBookEditionResponse(saved);
  }

  @Transactional
  public BookEditionResponse activateEdition(UUID id) {
    BookEdition edition =
        bookEditionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Edition not found with id: " + id));
    edition.setActive(true);
    BookEdition saved = bookEditionRepository.save(edition);
    return bookEditionMapper.toBookEditionResponse(saved);
  }

  @Transactional
  public BookEditionResponse deactivateEdition(UUID id) {
    BookEdition edition =
        bookEditionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Edition not found with id: " + id));
    edition.setActive(false);
    BookEdition saved = bookEditionRepository.save(edition);
    return bookEditionMapper.toBookEditionResponse(saved);
  }

  private BookFormat parseFormat(String format) {
    if (format == null) {
      return null;
    }
    try {
      return BookFormat.valueOf(format.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid format: " + format);
    }
  }
}
