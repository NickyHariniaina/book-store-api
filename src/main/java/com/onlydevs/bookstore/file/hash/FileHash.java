package com.onlydevs.bookstore.file.hash;

import com.onlydevs.bookstore.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
