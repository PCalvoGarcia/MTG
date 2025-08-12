package com.MagicTheGathering.deckCard.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class MaxCopiesAllowedException extends AppException {
    public MaxCopiesAllowedException() {
        super("Maximum 4 copies of a card allowed");
    }
}
