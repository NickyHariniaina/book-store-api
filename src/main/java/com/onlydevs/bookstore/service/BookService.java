package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookMapper;
import com.onlydevs.bookstore.model.Author;
import com.onlydevs.bookstore.model.Book;
import com.onlydevs.bookstore.model.BookAuthor;
import com.onlydevs.bookstore.model.Genre;
import com.onlydevs.bookstore.model.InventoryItem;
import com.onlydevs.bookstore.model.dto.request.CreateBookRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateBookRequest;
import com.onlydevs.bookstore.model.dto.response.BookAuthorResponse;
import com.onlydevs.bookstore.model.dto.response.BookDetailResponse;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.model.enums.BookLanguage;
import com.onlydevs.bookstore.model.exception.BadRequestException;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.AuthorRepository;
import com.onlydevs.bookstore.repository.BookAuthorRepository;
import com.onlydevs.bookstore.repository.BookEditionRepository;
import com.onlydevs.bookstore.repository.BookRepository;
import com.onlydevs.bookstore.repository.GenreRepository;
import com.onlydevs.bookstore.repository.InventoryItemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final AuthorRepository authorRepository;
  private final GenreRepository genreRepository;
  private final BookAuthorRepository bookAuthorRepository;
  private final BookEditionRepository bookEditionRepository;
  private final InventoryItemRepository inventoryItemRepository;

  public Page<BookSummaryResponse> getAllBooks(Pageable pageable) {
    return bookRepository.findAll(pageable).map(bookMapper::toBookSummaryResponse);
  }

  public BookDetailResponse getBookById(UUID id) {
    var book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));
    return bookMapper.toBookDetailResponse(book);
  }

  @Transactional
  public BookDetailResponse createBook(CreateBookRequest request) {
    var book =
        Book.builder()
            .title(request.getTitle())
            .summary(request.getSummary())
            .language(parseLanguage(request.getLanguage()))
            .coverUrl(request.getCoverUrl())
            .build();
    var saved = bookRepository.save(book);
    return bookMapper.toBookDetailResponse(saved);
  }

  @Transactional
  public BookDetailResponse updateBook(UUID id, UpdateBookRequest request) {
    var book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));
    if (request.getTitle() != null) {
      book.setTitle(request.getTitle());
    }
    if (request.getSummary() != null) {
      book.setSummary(request.getSummary());
    }
    if (request.getLanguage() != null) {
      book.setLanguage(parseLanguage(request.getLanguage()));
    }
    if (request.getCoverUrl() != null) {
      book.setCoverUrl(request.getCoverUrl());
    }
    var saved = bookRepository.save(book);
    return bookMapper.toBookDetailResponse(saved);
  }

  @Transactional
  public void deleteBook(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new NotFoundException("Book not found with id: " + id);
    }
    bookRepository.deleteById(id);
  }

  public Integer getBookTotalStock(UUID bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book not found with id: " + bookId);
    }
    return inventoryItemRepository.sumQuantityByBookId(bookId);
  }

  public List<InventoryItem> getAllLowStock() {
    return inventoryItemRepository.findAllLowStock();
  }

  @Transactional
  public BookAuthorResponse addAuthorToBook(UUID bookId, UUID authorId) {
    var book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));
    var author =
        authorRepository
            .findById(authorId)
            .orElseThrow(() -> new NotFoundException("Author not found with id: " + authorId));

    if (bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)) {
      throw new BadRequestException("Author already linked to this book");
    }

    var bookAuthor = BookAuthor.builder().book(book).author(author).build();
    var saved = bookAuthorRepository.save(bookAuthor);
    return bookMapper.toBookAuthorResponse(saved);
  }

  @Transactional
  public void removeAuthorFromBook(UUID bookId, UUID authorId) {
    var bookAuthor =
        bookAuthorRepository
            .findByBookIdAndAuthorId(bookId, authorId)
            .orElseThrow(() -> new NotFoundException("Author not linked to this book"));
    bookAuthorRepository.delete(bookAuthor);
  }

  @Transactional
  public void addGenreToBook(UUID bookId, UUID genreId) {
    var book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));
    var genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(() -> new NotFoundException("Genre not found with id: " + genreId));
    book.getGenres().add(genre);
    bookRepository.save(book);
  }

  @Transactional
  public void removeGenreFromBook(UUID bookId, UUID genreId) {
    var book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));
    var genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(() -> new NotFoundException("Genre not found with id: " + genreId));
    book.getGenres().remove(genre);
    bookRepository.save(book);
  }

  private BookLanguage parseLanguage(String language) {
    if (language == null) {
      return null;
    }
    try {
      return BookLanguage.valueOf(language.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid language: " + language);
    }
  }
}
