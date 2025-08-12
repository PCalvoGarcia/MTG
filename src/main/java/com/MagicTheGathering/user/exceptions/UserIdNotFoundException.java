package com.MagicTheGathering.user.exceptions;


import com.MagicTheGathering.Exceptions.AppException;

public class UserIdNotFoundException extends AppException {
    public UserIdNotFoundException(Long id) {
        super("This user id: " + id + " not found.");
    }
}
