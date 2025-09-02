package com.MagicTheGathering.Exceptions;

public class EmailSendException extends AppException {
    public EmailSendException() {
        super("Error sending email. ");
    }
}