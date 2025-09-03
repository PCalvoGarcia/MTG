package com.MagicTheGathering.like;

import com.MagicTheGathering.like.dto.DeckLikeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/deck/{id}/like")
@Tag(name = "Deck_like", description = "Operations related to likes in decks")
public class DeckLikeController {
    private final DeckLikeService deckLikeService;

    @PostMapping
    @Operation(summary = "Manage likes on deck.", responses = {
            @ApiResponse(responseCode = "200", description = "Manage likes successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckLikeResponse> manageLike(@PathVariable("id") Long deckId) {
        return new ResponseEntity<>(deckLikeService.manageLike(deckId), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get likes on deck.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck likes returned successfully"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/DeckNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DeckLikeResponse> getLikesByDeck(@PathVariable("id") Long deckId){
        return new ResponseEntity<>(deckLikeService.getLikesByDeckId(deckId), HttpStatus.OK);
    }

}
