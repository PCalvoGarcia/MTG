package com.MagicTheGathering.Exceptions;

public class UnauthorizedModificationsException extends AppException {
    public UnauthorizedModificationsException() {
        super("Unauthorized to modify");
    }
}
