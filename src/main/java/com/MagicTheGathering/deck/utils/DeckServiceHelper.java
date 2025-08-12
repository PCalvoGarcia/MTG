package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCartId.DeckCardId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeckServiceHelper {
    private final FormatValidationService formatValidationService;


    public void validateCardAddition(Deck deck, Card card, int quantity){
        formatValidationService.validateCardAddition(deck, card, quantity);

    }

    public void validateMaxCopiesLand(Card card, int totalQuantity){
        boolean isBasicLand = card.getTypes().toString().contains("BASIC_LAND");

        if (!isBasicLand && totalQuantity > 4){
            throw new RuntimeException("Maximum 4 copies of '" + card.getName() + "' allowed in deck");
        }
    }

}

