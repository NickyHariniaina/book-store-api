package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.InventoryItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  List<InventoryItem> findByBookStoreId(UUID storeId);

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  Optional<InventoryItem> findByBookStoreIdAndBookEditionId(UUID storeId, UUID editionId);
}
