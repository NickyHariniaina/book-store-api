package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.SaleItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {}
