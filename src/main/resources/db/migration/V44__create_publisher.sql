CREATE TABLE publisher
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(20)  NOT NULL,
    email      VARCHAR(255),
    country    VARCHAR(100),
    website    VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_publisher PRIMARY KEY (id)
);