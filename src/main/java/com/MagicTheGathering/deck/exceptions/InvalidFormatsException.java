package com.MagicTheGathering.deck.exceptions;

import com.MagicTheGathering.Exceptions.AppException;
import com.MagicTheGathering.legality.Legality;

public class InvalidFormatsException extends AppException {
    public InvalidFormatsException(String format, String legality) {
        super("Invalid format: " + format + ". Valid formats are: " + legality );
    }
}
