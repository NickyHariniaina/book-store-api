CREATE TABLE book
(
    id         UUID         NOT NULL,
    title      VARCHAR(255) NOT NULL,
    summary    VARCHAR(2000),
    language   VARCHAR(255),
    cover_url  VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book PRIMARY KEY (id)
);

CREATE TABLE book_genre
(
    book_id  UUID NOT NULL,
    genre_id UUID NOT NULL,
    CONSTRAINT pk_book_genre PRIMARY KEY (book_id, genre_id)
);

ALTER TABLE book_genre
    ADD CONSTRAINT fk_boogen_on_book FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE book_genre
    ADD CONSTRAINT fk_boogen_on_genre FOREIGN KEY (genre_id) REFERENCES genre (id);