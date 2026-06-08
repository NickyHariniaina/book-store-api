CREATE TABLE inventory_movement
(
    id                      UUID         NOT NULL,
    inventory_movement_type VARCHAR(255) NOT NULL,
    quantity                INTEGER      NOT NULL,
    reason                  VARCHAR(500) NOT NULL,
    reference               VARCHAR(255),
    moved_at                TIMESTAMP WITHOUT TIME ZONE,
    created_by              VARCHAR(100),
    book_store_id           UUID         NOT NULL,
    book_edition_id         UUID         NOT NULL,
    CONSTRAINT pk_inventory_movement PRIMARY KEY (id)
);

ALTER TABLE inventory_movement
    ADD CONSTRAINT FK_INVENTORY_MOVEMENT_ON_BOOK_EDITION FOREIGN KEY (book_edition_id) REFERENCES book_edition (id);

ALTER TABLE inventory_movement
    ADD CONSTRAINT FK_INVENTORY_MOVEMENT_ON_BOOK_STORE FOREIGN KEY (book_store_id) REFERENCES book_store (id);