package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.DeckService;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
@Tag(name = "Decks", description = "Operations related to decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService DECK_SERVICE;

    @GetMapping("/my-decks")
    @Operation(summary = "Get all my decks.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck list returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<DeckResponse>> getMyDecks() {
        List<DeckResponse> decks = DECK_SERVICE.getAllDeckByUser();
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

    @GetMapping("/public")
    @Operation(summary = "Get all public decks.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck list returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<DeckResponse>> getPublicDecks() {
        List<DeckResponse> publicDecks = DECK_SERVICE.getAllPublicDecks();
        return new ResponseEntity<>(publicDecks, HttpStatus.OK);
    }

    @GetMapping("/my-liked-decks")
    @Operation(summary = "Get all my public liked decks.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck list returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<DeckResponse>> getLikedDecksByLoggedUser(){
        List<DeckResponse> listLikedDecks = DECK_SERVICE.getLikedDecksByUser();
        return new ResponseEntity<>(listLikedDecks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my deck by id.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable Long id) {
        DeckResponse deck = DECK_SERVICE.getDeckById(id);
        return new ResponseEntity<>(deck, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create new deck.", responses = {
            @ApiResponse(responseCode = "201", description = "Deck returned successfully"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> createNewDeck(@Valid @RequestBody DeckRequest deckRequest) {
        DeckResponse newDeck = DECK_SERVICE.createDeck(deckRequest);
        return new ResponseEntity<>(newDeck, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update my deck.", responses = {
            @ApiResponse(responseCode = "201", description = "Deck returned successfully"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> updateDeckById(
            @PathVariable Long id,
            @Valid @RequestBody DeckRequest deckRequest
    ) {
        DeckResponse updatedDeck = DECK_SERVICE.updateDeck(id, deckRequest);
        return new ResponseEntity<>(updatedDeck, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete my deck.", responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteDeckById(@PathVariable Long id) {
        DECK_SERVICE.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{deckId}/cards")
    @Operation(summary = "Add my card on my deck.", responses = {
            @ApiResponse(responseCode = "201", description = "Add card to deck successfully"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> addCardToDeck(
            @PathVariable Long deckId,
            @Valid @RequestBody AddCardDeckRequest request
    ) {
        System.out.println("Adding card to deck " + deckId + ": " + request);

        DeckResponse updatedDeck = DECK_SERVICE.addCardToDeck(deckId, request);
        return new ResponseEntity<>(updatedDeck, HttpStatus.OK);
    }

    @DeleteMapping("/{deckId}/cards/{cardId}")
    @Operation(summary = "Add my card on my deck.", responses = {
            @ApiResponse(responseCode = "204", description = "Delete card to deck successfully"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> removeCardFromDeck(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        DeckResponse updatedDeck = DECK_SERVICE.removeCardFromDeck(deckId, cardId, quantity);
        return new ResponseEntity<>(updatedDeck, HttpStatus.OK);
    }
}

