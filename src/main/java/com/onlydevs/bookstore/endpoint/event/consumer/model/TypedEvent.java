package com.onlydevs.bookstore.endpoint.event.consumer.model;

import com.onlydevs.bookstore.PojaGenerated;
import com.onlydevs.bookstore.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
