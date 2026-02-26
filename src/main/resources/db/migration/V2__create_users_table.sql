CREATE TABLE users
(
    id            bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         varchar UNIQUE NOT NULL,
    name          varchar        NOT NULL,
    public_id     uuid UNIQUE    NOT NULL,
    password_hash varchar,
    auth_provider varchar        NOT NULL,
    provider_id   varchar,
    created_at    timestamptz    NOT NULL DEFAULT now(),
    updated_at    timestamptz    NOT NULL DEFAULT now()
);