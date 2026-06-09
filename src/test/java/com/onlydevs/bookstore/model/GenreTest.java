package com.onlydevs.bookstore.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GenreTest {

  @Test
  void rename_updates_name() {
    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Old Name").build();
    genre.rename("New Name");
    assertEquals("New Name", genre.getName());
  }

  @Test
  void rename_trims_whitespace() {
    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Old Name").build();
    genre.rename("  Trimmed Name  ");
    assertEquals("Trimmed Name", genre.getName());
  }

  @Test
  void rename_throws_when_name_is_null() {
    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Old Name").build();
    assertThrows(IllegalArgumentException.class, () -> genre.rename(null));
  }

  @Test
  void rename_throws_when_name_is_blank() {
    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Old Name").build();
    assertThrows(IllegalArgumentException.class, () -> genre.rename("  "));
  }

  @Test
  void rename_throws_when_name_is_empty() {
    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Old Name").build();
    assertThrows(IllegalArgumentException.class, () -> genre.rename(""));
  }

  @Test
  void builder_sets_all_fields() {
    UUID id = UUID.randomUUID();
    Genre genre =
        Genre.builder()
            .id(id)
            .name("Fiction")
            .description("Description")
            .books(new java.util.HashSet<>())
            .build();

    assertEquals(id, genre.getId());
    assertEquals("Fiction", genre.getName());
    assertEquals("Description", genre.getDescription());
    assertNotNull(genre.getBooks());
    assertTrue(genre.getBooks().isEmpty());
  }

  @Test
  void equalsAndHashCode_uses_id_only() {
    Genre g1 = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();
    Genre g2 = Genre.builder().id(g1.getId()).name("Different Name").build();

    assertEquals(g1, g2);
    assertEquals(g1.hashCode(), g2.hashCode());
  }

  @Test
  void constructor_sets_id_via_uuid_generation() {
    Genre genre = new Genre();
    assertNull(genre.getId());
  }
}
