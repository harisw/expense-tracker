package com.harisw.springexpensetracker.application.envelope.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateEnvelopeCommand(UUID envelopePublicId, String name, BigDecimal budget, Boolean canNotify) {
}
