package com.MagicTheGathering.user.exceptions;


import com.MagicTheGathering.Exceptions.AppException;

public class EmailAlreadyExistException extends AppException {
    public EmailAlreadyExistException(String email) {
        super("The email: " + email + " already exist");
    }
}
