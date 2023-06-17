CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(255) NOT NULL,
    requestor_id BIGINT REFERENCES users (id),
    created TIMESTAMP,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_available BOOLEAN,
    owner_id BIGINT REFERENCES users (id) NOT NULL,
    request_id BIGINT REFERENCES requests (id),
    CONSTRAINT pk_item PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT REFERENCES items (id) NOT NULL,
    booker_id BIGINT REFERENCES users(id) NOT NULL,
    status VARCHAR(50),
    CONSTRAINT pk_booking PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(255) NOT NULL,
    item_id BIGINT REFERENCES items (id) NOT NULL,
    author_id BIGINT REFERENCES users (id) NOT NULL,
    created TIMESTAMP,
    CONSTRAINT pk_comments PRIMARY KEY (id)
    );
