package com.MagicTheGathering.card.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class CardIdNotFoundException extends AppException {
    public CardIdNotFoundException(Long id) {
        super("Card witch id: " + id + " not found.");
    }
}
