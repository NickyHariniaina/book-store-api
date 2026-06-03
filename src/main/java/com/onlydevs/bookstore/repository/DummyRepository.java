package com.onlydevs.bookstore.repository;

import com.onlydevs.bookstore.PojaGenerated;
import com.onlydevs.bookstore.repository.model.Dummy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@PojaGenerated
@Repository
public interface DummyRepository extends JpaRepository<Dummy, String> {

  @Override
  List<Dummy> findAll();
}
