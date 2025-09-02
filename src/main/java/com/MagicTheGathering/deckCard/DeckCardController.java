package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deck-cards")
@RequiredArgsConstructor
@Tag(name = "Deck_cards", description = "Operations related to decks and cards on decks")
public class DeckCardController {

    private final DeckCardService DECK_CARD_SERVICE;

    @GetMapping("/deck/{deckId}")
    @Operation(summary = "Get list of my cards from my deck.", responses = {
            @ApiResponse(responseCode = "200", description = "Get cards from my deck returned successfully"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<DeckCardResponse>> getCardsByDeckId(@PathVariable Long deckId) {
        List<DeckCardResponse> deckCards = DECK_CARD_SERVICE.getCardsByDeckId(deckId);
        return new ResponseEntity<>(deckCards, HttpStatus.OK);
    }

    @GetMapping("/deck/{deckId}/card/{cardId}")
    @Operation(summary = "Get my card of my deck.", responses = {
            @ApiResponse(responseCode = "200", description = "Get card from my deck returned successfully"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<DeckCardResponse> getDeckCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId
    ) {
        DeckCardResponse deckCard = DECK_CARD_SERVICE.getDeckCard(deckId, cardId);
        return new ResponseEntity<>(deckCard, HttpStatus.OK);
    }

    @PutMapping("/deck/{deckId}/card/{cardId}/quantity")
    @Operation(summary = "Update my card quantity on my deck.", responses = {
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
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
     @Operation(summary = "Delete my card on my deck.", responses = {
            @ApiResponse(responseCode = "204", description = "Delete card to deck successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeDeckCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId
    ) {
        DECK_CARD_SERVICE.removeDeckCard(deckId, cardId);
        return ResponseEntity.noContent().build();
    }
}
