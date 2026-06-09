package com.onlydevs.bookstore.service.event;

import com.onlydevs.bookstore.PojaGenerated;
import com.onlydevs.bookstore.endpoint.event.model.UuidCreated;
import com.onlydevs.bookstore.repository.DummyUuidRepository;
import com.onlydevs.bookstore.repository.model.DummyUuid;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@PojaGenerated
@Service
@AllArgsConstructor
@Slf4j
public class UuidCreatedService implements Consumer<UuidCreated> {

  private final DummyUuidRepository dummyUuidRepository;

  @Override
  public void accept(UuidCreated uuidCreated) {
    var dummyUuid = new DummyUuid();
    dummyUuid.setId(uuidCreated.getUuid());
    dummyUuidRepository.save(dummyUuid);
  }
}
