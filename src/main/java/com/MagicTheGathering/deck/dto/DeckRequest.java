package com.MagicTheGathering.deck.dto;

import com.MagicTheGathering.legality.Legality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeckRequest(
        @NotBlank(message = "The deck name cannot be empty")
        @Size(min = 3, max = 50, message = "Deck name must be between 3 and 50 characters")
        String deckName,

        @NotNull(message = "Select an option to make the deck public or not.")
        Boolean isPublic,

        @NotNull(message = "Select the deck legality")
        Legality legalityEnum,

        Long userId
) {
}
