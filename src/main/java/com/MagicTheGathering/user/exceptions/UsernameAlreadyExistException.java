package com.MagicTheGathering.user.exceptions;


import com.MagicTheGathering.Exceptions.AppException;

public class UsernameAlreadyExistException extends AppException {
    public UsernameAlreadyExistException(String username) {
        super("This username: " + username + " already exist.");
    }
}
