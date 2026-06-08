CREATE TABLE book_edition
(
    id           UUID         NOT NULL,
    isbn         VARCHAR(13)  NOT NULL,
    edition      VARCHAR(50),
    format       VARCHAR(255) NOT NULL,
    active       BOOLEAN,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    publisher_id UUID,
    book_id      UUID         NOT NULL,
    CONSTRAINT pk_book_edition PRIMARY KEY (id)
);

ALTER TABLE book_edition
    ADD CONSTRAINT uc_book_edition_isbn UNIQUE (isbn);

ALTER TABLE book_edition
    ADD CONSTRAINT FK_BOOK_EDITION_ON_BOOK FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE book_edition
    ADD CONSTRAINT FK_BOOK_EDITION_ON_PUBLISHER FOREIGN KEY (publisher_id) REFERENCES publisher (id);