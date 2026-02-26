ALTER TABLE "envelopes"
    ADD FOREIGN KEY ("id") REFERENCES "expenses" ("envelope_id");
