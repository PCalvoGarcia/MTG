package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.legality.Legality;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormatValidationService {

    public void validateCardAddition(Deck deck, Card card, int quantity) {
        Legality format = deck.getType();

        if (!card.getLegalityFormat().contains(format)) {
            throw new RuntimeException("Card '" + card.getName() + "' is not legal in " + format + " format");
        }

        validateCardLimits(deck, card, quantity, format);

        validateDeckSize(deck, quantity, format);
    }

    private void validateCardLimits(Deck deck, Card card, int additionalQuantity, Legality format) {
        int currentQuantity = deck.getDeckCards().stream()
                .filter(deckCard -> deckCard.getCard().getId().equals(card.getId()))
                .mapToInt(DeckCard::getQuantity)
                .sum();

        int totalQuantity = currentQuantity + additionalQuantity;

        switch (format) {
            case STANDARD, MODERN, PIONEER, LEGACY_VINTAGE, PAUPER -> {
                validateConstructedFormat(card, totalQuantity);
            }
            case COMMANDER, BRAWL -> {
                validateSingletonFormat(card, totalQuantity, format);
            }
            case BOOSTER_DRAFT, SEALED_DECK -> {
                validateLimitedFormat(card, totalQuantity);
            }
            default -> {
                validateConstructedFormat(card, totalQuantity); // Default a constructed
            }
        }
    }

    private void validateLimitedFormat(Card card, int totalQuantity) {
    }

    private void validateConstructedFormat(Card card, int quantity) {
        boolean isBasicLand = isBasicLand(card);

        if (!isBasicLand && quantity > 4) {
            throw new RuntimeException("Maximum 4 copies of '" + card.getName() + "' allowed in constructed formats");
        }
    }

    private void validateSingletonFormat(Card card, int quantity, Legality format) {
        boolean isBasicLand = isBasicLand(card);
        boolean isCommander = isCommander(card, format);

        if (!isBasicLand && !isCommander && quantity > 1) {
            throw new RuntimeException("Only 1 copy of '" + card.getName() + "' allowed in " + format + " format (singleton)");
        }
        if (isCommander && quantity > 1) {
            throw new RuntimeException("Only 1 commander allowed");
        }
    }

    private void validateDeckSize(Deck deck, int additionalCards, Legality format) {
        int currentSize = deck.getDeckCards().stream()
                .mapToInt(DeckCard::getQuantity)
                .sum();

        int newSize = currentSize + additionalCards;
        int maxCards = getMaxCardsForFormat(format);
        int minCards = getMinCardsForFormat(format);

        if (newSize > maxCards) {
            throw new RuntimeException("Adding " + additionalCards + " cards would exceed maximum deck size of " + maxCards + " for " + format);
        }
    }

    private int getMaxCardsForFormat(Legality format) {
        return switch (format) {
            case STANDARD, MODERN, PIONEER, LEGACY_VINTAGE, PAUPER -> 75;
            case COMMANDER -> 100;
            case BRAWL -> 60;
            case BOOSTER_DRAFT, SEALED_DECK -> 40;
            default -> 60;
        };
    }

    private int getMinCardsForFormat(Legality format) {
        return switch (format) {
            case STANDARD, MODERN, PIONEER, LEGACY_VINTAGE, PAUPER -> 60;
            case COMMANDER -> 100;
            case BRAWL -> 60;
            case BOOSTER_DRAFT, SEALED_DECK -> 40;
            default -> 60;
        };
    }

    private boolean isBasicLand(Card card) {
        return card.getTypes().toString().contains("BASIC_LAND") &&
                card.getTypes().toString().contains("LAND");
    }

    private boolean isCommander(Card card, Legality format) {
        if (format != Legality.COMMANDER && format != Legality.BRAWL) {
            return false;
        }

        return card.getTypes().toString().contains("COMMANDER");
    }

    public boolean allowsMultipleCopies(Legality format) {
        return switch (format) {
            case STANDARD, MODERN, PIONEER, LEGACY_VINTAGE, PAUPER, BOOSTER_DRAFT, SEALED_DECK -> true;
            case COMMANDER, BRAWL -> false;
            default -> true;
        };
    }

    public int getMaxCopiesPerCard(Legality format) {
        return switch (format) {
            case STANDARD, MODERN, PIONEER, LEGACY_VINTAGE, PAUPER -> 4;
            case COMMANDER, BRAWL -> 1;
            case BOOSTER_DRAFT, SEALED_DECK -> Integer.MAX_VALUE;
            default -> 4;
        };
    }
}
