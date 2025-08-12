package com.MagicTheGathering.deck.exceptions;

import com.MagicTheGathering.Exceptions.AppException;
import com.MagicTheGathering.legality.Legality;

public class MaxCopiesAllowedFormatException extends AppException {
    public MaxCopiesAllowedFormatException(String cardName, Legality format) {
        super("Only 1 copy of '" + cardName + "' allowed in " + format + " format (singleton)");
    }
}
