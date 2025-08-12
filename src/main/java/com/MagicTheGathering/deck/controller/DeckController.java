package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.DeckService;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService DECK_SERVICE;

    @GetMapping("/my-decks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DeckResponse>> getMyDecks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Page<DeckResponse> decks = DECK_SERVICE.getAllDeckByUser(convertToZeroBasedPage(page), size);
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<DeckResponse>> getPublicDecks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Page<DeckResponse> publicDecks = DECK_SERVICE.getAllPublicDecks(convertToZeroBasedPage(page), size);
        return new ResponseEntity<>(publicDecks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable Long id) {
        DeckResponse deck = DECK_SERVICE.getDeckById(id);
        return new ResponseEntity<>(deck, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> createNewDeck(@Valid @RequestBody DeckRequest deckRequest) {
        DeckResponse newDeck = DECK_SERVICE.createDeck(deckRequest);
        return new ResponseEntity<>(newDeck, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> updateDeckById(
            @PathVariable Long id,
            @Valid @RequestBody DeckRequest deckRequest
    ) {
        DeckResponse updatedDeck = DECK_SERVICE.updateDeck(id, deckRequest);
        return new ResponseEntity<>(updatedDeck, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteDeckById(@PathVariable Long id) {
        DECK_SERVICE.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{deckId}/cards")
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckResponse> removeCardFromDeck(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        DeckResponse updatedDeck = DECK_SERVICE.removeCardFromDeck(deckId, cardId, quantity);
        return new ResponseEntity<>(updatedDeck, HttpStatus.OK);
    }

    private int convertToZeroBasedPage(int page) {
        return page - 1;
    }
}

