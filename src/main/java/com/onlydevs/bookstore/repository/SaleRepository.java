package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.Sale;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
  List<Sale> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
