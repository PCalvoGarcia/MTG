package com.MagicTheGathering.deckCard.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class DeckIdNotFoundException extends AppException {
    public DeckIdNotFoundException(Long deckId) {
        super("Deck not found with id: " + deckId);
    }
}
