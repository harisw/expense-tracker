package com.harisw.springexpensetracker.application.envelope.dto.request;

import com.harisw.springexpensetracker.application.envelope.dto.command.CreateEnvelopeCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateEnvelopeRequest(@Nullable UUID envelopeParentPublicId, @NotNull String name,
                                    @NotNull Boolean isTemplate, @NotNull BigDecimal budget,
                                    @NotNull Boolean canNotify) {

    /**
     * @return CreateExpenseCommand
     */
    public CreateEnvelopeCommand toCommand() {
        return new CreateEnvelopeCommand(envelopeParentPublicId, name, isTemplate, budget, canNotify);
    }
}
