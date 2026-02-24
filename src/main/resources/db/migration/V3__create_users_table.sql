CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID         NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER',
    password_hash VARCHAR(255),
    auth_provider VARCHAR(50)  NOT NULL DEFAULT 'LOCAL',
    provider_id   VARCHAR(255),
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);