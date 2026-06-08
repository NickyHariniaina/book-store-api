CREATE TABLE sale
(
    id             UUID         NOT NULL,
    status         VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255),
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    book_store_id  UUID         NOT NULL,
    customer_id    UUID,
    CONSTRAINT pk_sale PRIMARY KEY (id)
);

ALTER TABLE sale
    ADD CONSTRAINT FK_SALE_ON_BOOK_STORE FOREIGN KEY (book_store_id) REFERENCES book_store (id);

ALTER TABLE sale
    ADD CONSTRAINT FK_SALE_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id);