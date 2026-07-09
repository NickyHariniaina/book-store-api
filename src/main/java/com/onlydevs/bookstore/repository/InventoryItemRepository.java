package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.InventoryItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

  List<InventoryItem> findByBookEditionId(UUID editionId);

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  List<InventoryItem> findByBookStoreId(UUID storeId);

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  Optional<InventoryItem> findByBookStoreIdAndBookEditionId(UUID storeId, UUID editionId);

  @Query(
      "SELECT COALESCE(SUM(i.quantityOnHand), 0) FROM InventoryItem i WHERE i.bookEdition.book.id ="
          + " :bookId")
  Integer sumQuantityByBookId(@Param("bookId") UUID bookId);

  @Query(
      "SELECT COALESCE(SUM(i.quantityOnHand), 0) FROM InventoryItem i WHERE i.bookStore.id ="
          + " :storeId AND i.bookEdition.book.id = :bookId")
  Integer sumQuantityByStoreIdAndBookId(
      @Param("storeId") UUID storeId, @Param("bookId") UUID bookId);

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  @Query("SELECT i FROM InventoryItem i WHERE i.quantityOnHand <= i.reorderLevel")
  List<InventoryItem> findAllLowStock();

  @EntityGraph(attributePaths = {"bookEdition.book", "bookStore"})
  @Query(
      "SELECT i FROM InventoryItem i WHERE i.bookStore.id = :storeId AND i.quantityOnHand <="
          + " i.reorderLevel")
  List<InventoryItem> findLowStockByStoreId(@Param("storeId") UUID storeId);
}
