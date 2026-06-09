package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.InventoryMovement;
import com.onlydevs.bookstore.model.enums.InventoryMovementType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {

  List<InventoryMovement> findByBookStoreId(UUID storeId);

  List<InventoryMovement> findByBookStoreIdAndInventoryMovementType(
      UUID storeId, InventoryMovementType type);

  List<InventoryMovement> findByBookEditionId(UUID editionId);
}
