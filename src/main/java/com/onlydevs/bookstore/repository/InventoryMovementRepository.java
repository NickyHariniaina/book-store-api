package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.model.InventoryMovement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {

  @EntityGraph(attributePaths = {"bookEdition.book"})
  List<InventoryMovement> findByBookEditionId(UUID editionId);
}
