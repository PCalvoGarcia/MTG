package com.MagicTheGathering.deckCard.dto;

import com.MagicTheGathering.card.dto.CardResponse;

public record DeckCardResponse(
        CardResponse card,
        int quantity
) {
}
