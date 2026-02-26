ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_envelope_id FOREIGN KEY (envelope_id) REFERENCES envelopes (id);
CREATE INDEX ON expenses(envelope_id);