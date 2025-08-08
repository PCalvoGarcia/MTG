package com.MagicTheGathering.deck.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCardDeckRequest(
        @NotNull
        Long cardId,

        @Positive
        int quantity
) {
}
