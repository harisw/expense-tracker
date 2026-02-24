CREATE TABLE expenses
(
    id          BIGSERIAL PRIMARY KEY,
    public_id   UUID           NOT NULL UNIQUE,
    category    VARCHAR(50)    NOT NULL,
    description VARCHAR(255)   NOT NULL,
    amount      NUMERIC(12, 2) NOT NULL,
    date        DATE           NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);