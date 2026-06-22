ALTER TABLE publisher
    ADD CONSTRAINT uc_publisher_email UNIQUE (email);
