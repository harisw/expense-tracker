package com.harisw.springexpensetracker.application.envelope.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateEnvelopeCommand(UUID envelopeParentPublicId, String name,
                                    Boolean isTemplate, BigDecimal budget, Boolean canNotify) {
}
