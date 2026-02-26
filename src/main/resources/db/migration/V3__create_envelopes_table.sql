CREATE TABLE envelopes
(
    id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     bigint         NOT NULL,
    envelope_id bigint,
    public_id   uuid UNIQUE    NOT NULL,
    name        varchar        NOT NULL,
    is_template bool NOT NULL default false,
    budget      NUMERIC(19, 4) NOT NULL,
    can_notify  bool NOT NULL default false,
    created_at  timestamptz    NOT NULL DEFAULT now(),
    updated_at  timestamptz    NOT NULL DEFAULT now()
);