CREATE TABLE genre
(
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT pk_genre PRIMARY KEY (id)
);

ALTER TABLE genre
    ADD CONSTRAINT uc_genre_name UNIQUE (name);