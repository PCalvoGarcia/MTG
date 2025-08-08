package com.MagicTheGathering.deckCard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DeckCardRequest(
        @NotNull(message = "Deck ID cannot be null")
        @Positive(message = "Deck ID must be positive")
        Long deckId,

        @NotNull(message = "Card ID cannot be null")
        @Positive(message = "Card ID must be positive")
        Long cardId,

        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 4, message = "Maximum 4 copies allowed")
        int quantity
) {
}
