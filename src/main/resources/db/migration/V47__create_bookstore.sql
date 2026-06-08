CREATE TABLE book_store
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    address    VARCHAR(500),
    phone      VARCHAR(20),
    email      VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_store PRIMARY KEY (id)
);

ALTER TABLE book_store
    ADD CONSTRAINT uc_book_store_email UNIQUE (email);

ALTER TABLE book_store
    ADD CONSTRAINT uc_book_store_name UNIQUE (name);

ALTER TABLE book_store
    ADD CONSTRAINT uc_book_store_phone UNIQUE (phone);