package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Sale;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, UUID> {}
