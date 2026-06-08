CREATE TABLE sale_item
(
    id               UUID           NOT NULL,
    quantity         INTEGER        NOT NULL,
    unit_price       DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    sale_id          UUID           NOT NULL,
    book_edition_id  UUID           NOT NULL,
    CONSTRAINT pk_sale_item PRIMARY KEY (id)
);

ALTER TABLE sale_item
    ADD CONSTRAINT FK_SALE_ITEM_ON_BOOK_EDITION FOREIGN KEY (book_edition_id) REFERENCES book_edition (id);

ALTER TABLE sale_item
    ADD CONSTRAINT FK_SALE_ITEM_ON_SALE FOREIGN KEY (sale_id) REFERENCES sale (id);