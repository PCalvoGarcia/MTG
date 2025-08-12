package com.MagicTheGathering.deck.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class MaxCommanderException extends AppException {
    public MaxCommanderException() {
        super("Only 1 commander allowed");
    }
}
