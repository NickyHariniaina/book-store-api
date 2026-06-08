CREATE TABLE book_author
(
    id                 UUID NOT NULL,
    book_id            UUID NOT NULL,
    author_id          UUID NOT NULL,
    role               VARCHAR(255),
    contribution_order INTEGER,
    CONSTRAINT pk_book_author PRIMARY KEY (id)
);

ALTER TABLE book_author
    ADD CONSTRAINT uc_0a3060d62182ea79a8dec3c55 UNIQUE (book_id, author_id);

ALTER TABLE book_author
    ADD CONSTRAINT FK_BOOK_AUTHOR_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES author (id);

ALTER TABLE book_author
    ADD CONSTRAINT FK_BOOK_AUTHOR_ON_BOOK FOREIGN KEY (book_id) REFERENCES book (id);