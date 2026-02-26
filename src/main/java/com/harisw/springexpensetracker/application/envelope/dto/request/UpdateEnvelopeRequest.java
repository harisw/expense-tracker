package com.harisw.springexpensetracker.application.envelope.dto.request;

import com.harisw.springexpensetracker.application.envelope.dto.command.UpdateEnvelopeCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateEnvelopeRequest(@Nullable UUID envelopeParentPublicId, @NotNull String name,
                                    @NotNull BigDecimal budget,
                                    @NotNull Boolean canNotify) {

    /**
     * @return CreateExpenseCommand
     */
    public UpdateEnvelopeCommand toCommand() {
        return new UpdateEnvelopeCommand(envelopeParentPublicId, name, budget, canNotify);
    }
}
