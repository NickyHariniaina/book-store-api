package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Publisher;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
  boolean existsByEmailIgnoreCase(String email);
}
