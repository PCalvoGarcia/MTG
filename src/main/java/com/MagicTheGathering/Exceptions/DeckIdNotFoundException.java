package com.MagicTheGathering.Exceptions;

public class DeckIdNotFoundException extends AppException {
    public DeckIdNotFoundException(Long deckId) {
        super("Deck not found with id: " + deckId);
    }
}
