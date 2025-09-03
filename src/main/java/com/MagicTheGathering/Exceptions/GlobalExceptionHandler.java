package com.MagicTheGathering.Exceptions;

import com.MagicTheGathering.card.exceptions.CardIdNotFoundException;
import com.MagicTheGathering.card.exceptions.DeleteCardNotAllowedException;
import com.MagicTheGathering.deck.exceptions.IllegalCardException;
import com.MagicTheGathering.deck.exceptions.InvalidFormatsException;
import com.MagicTheGathering.deck.exceptions.MaxCommanderException;
import com.MagicTheGathering.deck.exceptions.MaxCopiesAllowedFormatException;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
import com.MagicTheGathering.user.exceptions.EmailAlreadyExistException;
import com.MagicTheGathering.user.exceptions.UserIdNotFoundException;
import com.MagicTheGathering.user.exceptions.UsernameAlreadyExistException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, String>> handleAppException(AppException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EmptyListException.class)
    public ResponseEntity<String> handleEmptyList(EmptyListException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedModificationsException.class)
    public ResponseEntity<String> handleUnauthorizedModifications(UnauthorizedModificationsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<String> handleUsernameAlreadyExist(UsernameAlreadyExistException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<String> handleEmailAlreadyExist(EmailAlreadyExistException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<String> handleUserIdNotFound(UserIdNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardIdNotFoundException.class)
    public ResponseEntity<String> handleCardIdNotFound(CardIdNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeckIdNotFoundException.class)
    public ResponseEntity<String> handleDeckIdNotFound(DeckIdNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedPrivateDeckException.class)
    public ResponseEntity<String> handleAccessDeniedPrivateDeck(AccessDeniedPrivateDeckException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardIdNotFoundInDeckException.class)
    public ResponseEntity<String> handleCardIdNotFoundInDeck(CardIdNotFoundInDeckException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxCopiesAllowedException.class)
    public ResponseEntity<String> handleMaxCopiesAllowed(MaxCopiesAllowedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalCardException.class)
    public ResponseEntity<String> handleIllegalCard(IllegalCardException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxCommanderException.class)
    public ResponseEntity<String> handleMaxCommander(MaxCommanderException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxCopiesAllowedFormatException.class)
    public ResponseEntity<String> handleMaxCopiesAllowedFormat(MaxCopiesAllowedFormatException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeleteCardNotAllowedException.class)
    public ResponseEntity<String> handleDeleteCardNotAllowed(DeleteCardNotAllowedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidFormatsException.class)
    public ResponseEntity<String> handleInvalidFormats(InvalidFormatsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<String> handleEmailSendException(EmailSendException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; \n"));

        return new ResponseEntity<>("Validation failed: \n" + errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<String> handleInvalidFormatException(InvalidFormatException e) {
        String message = e.getOriginalMessage();
        return new ResponseEntity<>("Error in role: \n" + message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
    }
}
