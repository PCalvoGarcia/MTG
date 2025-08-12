package com.MagicTheGathering.deck.exceptions;

import com.MagicTheGathering.Exceptions.AppException;
import com.MagicTheGathering.legality.Legality;

public class IllegalCardException extends AppException {
    public IllegalCardException(String cardName, Legality format) {
        super("Card '" + cardName + "' is not legal in " + format + " format");
    }
}
