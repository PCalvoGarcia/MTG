package com.MagicTheGathering.card.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class DeleteCardNotAllowedException extends AppException {
    public DeleteCardNotAllowedException() {
        super("Cannot delete card: it is currently used in one or more decks");
    }
}
