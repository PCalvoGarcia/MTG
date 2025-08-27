package com.MagicTheGathering.Exceptions;

import com.MagicTheGathering.card.exceptions.CardIdNotFoundException;
import com.MagicTheGathering.card.exceptions.DeleteCardNotAllowedException;
import com.MagicTheGathering.deck.exceptions.IllegalCardException;
import com.MagicTheGathering.deck.exceptions.InvalidFormatsException;
import com.MagicTheGathering.deck.exceptions.MaxCommanderException;
import com.MagicTheGathering.deck.exceptions.MaxCopiesAllowedFormatException;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.deckCard.exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.exceptions.EmailAlreadyExistException;
import com.MagicTheGathering.user.exceptions.UserIdNotFoundException;
import com.MagicTheGathering.user.exceptions.UsernameAlreadyExistException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTestThrow {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleAppException() {
        AppException ex = new AppException("App error");
        ResponseEntity<?> response = globalExceptionHandler.handleAppException(ex);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(((HashMap<?, ?>) response.getBody()).get("error")).isEqualTo("App error");
    }

    @Test
    void handleEmptyList() {
        ResponseEntity<String> response = globalExceptionHandler.handleEmptyList(new EmptyListException());
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        assertThat(response.getBody()).isEqualTo("The list is empty");
    }

    @Test
    void handleUnauthorizedAccess() {
        ResponseEntity<String> response = globalExceptionHandler.handleUnauthorizedAccess(new UnauthorizedAccessException());
        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo("You are not authorized to access");
    }

    @Test
    void handleUnauthorizedModifications() {
        ResponseEntity<String> response = globalExceptionHandler.handleUnauthorizedModifications(new UnauthorizedModificationsException());
        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo("Unauthorized to modify");
    }

    @Test
    void handleUsernameAlreadyExist() {
        ResponseEntity<String> response = globalExceptionHandler.handleUsernameAlreadyExist(new UsernameAlreadyExistException("User exists"));
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("This username: User exists already exist.");
    }

    @Test
    void handleEmailAlreadyExist() {
        ResponseEntity<String> response = globalExceptionHandler.handleEmailAlreadyExist(new EmailAlreadyExistException("Email exists"));
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("The email: Email exists already exist");
    }

    @Test
    void handleUserIdNotFound() {
        ResponseEntity<String> response = globalExceptionHandler.handleUserIdNotFound(new UserIdNotFoundException(1L));
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("This user id: 1 not found.");
    }

    @Test
    void handleCardIdNotFound() {
        ResponseEntity<String> response = globalExceptionHandler.handleCardIdNotFound(new CardIdNotFoundException(1L));
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Card witch id: 1 not found.");
    }

    @Test
    void handleDeckIdNotFound() {
        ResponseEntity<String> response = globalExceptionHandler.handleDeckIdNotFound(new DeckIdNotFoundException(1L));
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Deck not found with id: 1");
    }

    @Test
    void handleAccessDeniedPrivateDeck() {
        ResponseEntity<String> response = globalExceptionHandler.handleAccessDeniedPrivateDeck(new AccessDeniedPrivateDeckException());
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Access denied to private deck");
    }

    @Test
    void handleCardIdNotFoundInDeck() {
        ResponseEntity<String> response = globalExceptionHandler.handleCardIdNotFoundInDeck(new CardIdNotFoundInDeckException());
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Card not found in deck");
    }

    @Test
    void handleMaxCopiesAllowed() {
        ResponseEntity<String> response = globalExceptionHandler.handleMaxCopiesAllowed(new MaxCopiesAllowedException());
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Maximum 4 copies of a card allowed");
    }

    @Test
    void handleIllegalCard() {
        ResponseEntity<String> response = globalExceptionHandler.handleIllegalCard(new IllegalCardException("commander", Legality.COMMANDER));
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("Card 'commander' is not legal in COMMANDER format");
    }

    @Test
    void handleMaxCommander() {
        ResponseEntity<String> response = globalExceptionHandler.handleMaxCommander(new MaxCommanderException());
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("Only 1 commander allowed");
    }

    @Test
    void handleMaxCopiesAllowedFormat() {
        ResponseEntity<String> response = globalExceptionHandler.handleMaxCopiesAllowedFormat(new MaxCopiesAllowedFormatException("commander", Legality.COMMANDER));
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("Only 1 copy of 'commander' allowed in COMMANDER format (singleton)");
    }

    @Test
    void handleDeleteCardNotAllowed() {
        ResponseEntity<String> response = globalExceptionHandler.handleDeleteCardNotAllowed(new DeleteCardNotAllowedException());
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("Cannot delete card: it is currently used in one or more decks");
    }

    @Test
    void handleInvalidFormats() {
        ResponseEntity<String> response = globalExceptionHandler.handleInvalidFormats(new InvalidFormatsException("COMMANDER", "COMMANDER"));
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isEqualTo("Invalid format: COMMANDER. Valid formats are: COMMANDER");
    }

    @Test
    void handleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("object", "field", "must not be blank")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<String> response = globalExceptionHandler.handleValidationErrors(ex);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("Validation failed");
    }

    @Test
    void handleInvalidFormatException() {
        InvalidFormatException ex = mock(InvalidFormatException.class);
        when(ex.getOriginalMessage()).thenReturn("Invalid format");

        ResponseEntity<String> response = globalExceptionHandler.handleInvalidFormatException(ex);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Error in role: \nInvalid format");
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violated");

        ResponseEntity<String> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).contains("Constraint violated");
    }
}
