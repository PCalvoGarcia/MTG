package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deck-cards")
@RequiredArgsConstructor
public class DeckCardController {

    private final DeckCardService DECK_CARD_SERVICE;

    @GetMapping("/deck/{deckId}")
    public ResponseEntity<List<DeckCardResponse>> getCardsByDeckId(@PathVariable Long deckId) {
        List<DeckCardResponse> deckCards = DECK_CARD_SERVICE.getCardsByDeckId(deckId);
        return new ResponseEntity<>(deckCards, HttpStatus.OK);
    }

    @GetMapping("/deck/{deckId}/card/{cardId}")
    public ResponseEntity<DeckCardResponse> getDeckCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId
    ) {
        DeckCardResponse deckCard = DECK_CARD_SERVICE.getDeckCard(deckId, cardId);
        return new ResponseEntity<>(deckCard, HttpStatus.OK);
    }

    @PutMapping("/deck/{deckId}/card/{cardId}/quantity")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckCardResponse> updateCardQuantity(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @RequestParam int quantity
    ) {
        DeckCardResponse updatedDeckCard = DECK_CARD_SERVICE.updateDeckCardQuantity(deckId, cardId, quantity);

        if (updatedDeckCard == null) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(updatedDeckCard, HttpStatus.OK);
    }

    @DeleteMapping("/deck/{deckId}/card/{cardId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeDeckCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId
    ) {
        DECK_CARD_SERVICE.removeDeckCard(deckId, cardId);
        return ResponseEntity.noContent().build();
    }
}
