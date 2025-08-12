package com.MagicTheGathering.deckCard.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class CardIdNotFoundInDeckException extends AppException {
    public CardIdNotFoundInDeckException() {
        super("Card not found in deck");
    }
}
