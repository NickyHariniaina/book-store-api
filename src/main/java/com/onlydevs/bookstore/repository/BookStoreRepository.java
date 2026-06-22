package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.BookStore;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookStoreRepository extends JpaRepository<BookStore, UUID> {}
