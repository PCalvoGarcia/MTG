package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks/search")
@RequiredArgsConstructor
@Tag(name = "Deck_search", description = "Operations related to search decks")
public class DeckSearchController {

    private final DeckSearchService DECK_SEARCH_SERVICE;

    @GetMapping("/by-format/{format}")
    @Operation(summary = "Get decks by format.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck list returned successfully"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<DeckResponse>> getDecksByFormat(
            @PathVariable String format
    ) {
        List<DeckResponse> decks = DECK_SEARCH_SERVICE.getDecksByFormat(format);
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
        @Operation(summary = "Get all public decks by user.", responses = {
            @ApiResponse(responseCode = "200", description = "Deck list returned successfully"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/UserNotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<DeckResponse>> getPublicDecksByUser(
            @PathVariable Long userId
    ) {
        List<DeckResponse> decks = DECK_SEARCH_SERVICE.getPublicDecksByUser(userId);
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

}
