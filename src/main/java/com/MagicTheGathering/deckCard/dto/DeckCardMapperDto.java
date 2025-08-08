package com.MagicTheGathering.deckCard.dto;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.DeckCard;

public class DeckCardMapperDto {
    public static DeckCardResponse fromEntity(DeckCard deckCard) {
        if (deckCard == null) {
            return null;
        }

        return new DeckCardResponse(
                CardMapperDto.fromEntity(deckCard.getCard()),
                deckCard.getQuantity()
        );
    }

    public static DeckCard toEntity(DeckCardRequest request, Card card, Deck deck) {
        if (request == null || card == null || deck == null) {
            return null;
        }

        return DeckCard.builder()
                .card(card)
                .deck(deck)
                .quantity(request.quantity())
                .build();
    }
}
