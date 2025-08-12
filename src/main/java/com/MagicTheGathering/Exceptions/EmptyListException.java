package com.MagicTheGathering.Exceptions;

public class EmptyListException extends AppException {
    public EmptyListException() {
        super("The list is empty");
    }
}
