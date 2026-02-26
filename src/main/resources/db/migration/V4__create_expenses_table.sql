CREATE TABLE expenses
(
    id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    envelope_id bigint         NOT NULL,
    public_id   uuid UNIQUE    NOT NULL,
    description varchar,
    amount      numeric(19, 4) NOT NULL,
    date        date NOT NULL,
    created_at  timestamptz    NOT NULL DEFAULT now(),
    updated_at  timestamptz    NOT NULL DEFAULT now()
);