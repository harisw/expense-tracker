package com.harisw.springexpensetracker.domain.envelope;

import java.util.UUID;

public class EnvelopeNotFoundException extends RuntimeException {
    private final UUID publicId;

    public EnvelopeNotFoundException(UUID publicId) {
        super("Envelope not found: " + publicId);
        this.publicId = publicId;
    }

    public UUID getPublicId() {
        return publicId;
    }
}
