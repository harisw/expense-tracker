ALTER TABLE envelopes
    ADD CONSTRAINT fk_envelopes_user_id FOREIGN KEY (user_id) REFERENCES users (id);
CREATE INDEX ON envelopes(user_id);

ALTER TABLE envelopes
    ADD CONSTRAINT fk_envelopes_parent_envelope_id FOREIGN KEY (envelope_id) REFERENCES envelopes (id);
CREATE INDEX ON envelopes(envelope_id);
