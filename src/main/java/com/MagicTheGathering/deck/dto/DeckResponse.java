package com.MagicTheGathering.deck.dto;

import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;

import java.util.List;

public record DeckResponse(
        Long id,
        String deckName,
        Boolean isPublic,
        String type,
        int maxCards,
        int totalCards,
        Long userId,
        List<DeckCardResponse> cards
) {
}
