CREATE TABLE inventory_item
(
    id               UUID    NOT NULL,
    quantity_on_hand INTEGER NOT NULL,
    reorder_level    INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    version          BIGINT,
    book_store_id    UUID    NOT NULL,
    book_edition_id  UUID    NOT NULL,
    CONSTRAINT pk_inventory_item PRIMARY KEY (id)
);

ALTER TABLE inventory_item
    ADD CONSTRAINT FK_INVENTORY_ITEM_ON_BOOK_EDITION FOREIGN KEY (book_edition_id) REFERENCES book_edition (id);

ALTER TABLE inventory_item
    ADD CONSTRAINT FK_INVENTORY_ITEM_ON_BOOK_STORE FOREIGN KEY (book_store_id) REFERENCES book_store (id);