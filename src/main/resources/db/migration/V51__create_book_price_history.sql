CREATE TABLE book_price_history
(
    id              UUID                        NOT NULL,
    price           DECIMAL(10, 2)              NOT NULL,
    effective_from  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    effective_to    TIMESTAMP WITHOUT TIME ZONE,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    book_edition_id UUID                        NOT NULL,
    CONSTRAINT pk_book_price_history PRIMARY KEY (id)
);

ALTER TABLE book_price_history
    ADD CONSTRAINT FK_BOOK_PRICE_HISTORY_ON_BOOK_EDITION FOREIGN KEY (book_edition_id) REFERENCES book_edition (id);