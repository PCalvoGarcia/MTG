package com.MagicTheGathering.deckCard.exceptions;

import com.MagicTheGathering.Exceptions.AppException;

public class AccessDeniedPrivateDeckException extends AppException {

    public AccessDeniedPrivateDeckException() {
        super("Access denied to private deck");
    }
}
