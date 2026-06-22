package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.InventoryItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

  List<InventoryItem> findByBookEditionId(UUID editionId);
}
