package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.BookMapper;
import com.onlydevs.bookstore.model.dto.response.BookSummaryResponse;
import com.onlydevs.bookstore.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class BookService {
    private BookRepository bookRepository;
    private BookMapper bookMapper;

    public Page<BookSummaryResponse> getAllBooks(Pageable pageable){
        return bookRepository.findAll(pageable).map(bookMapper::toBookSummaryResponse);
    }
}
