package com.MagicTheGathering.card;

import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Operations related to cards")
public class CardController {
    private final CardService CARD_SERVICE;

    @GetMapping("/my-cards")
    @Operation(summary = "Get all my cards.", responses = {
            @ApiResponse(responseCode = "200", description = "Card list returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardResponse>> getMyCards(){
        List<CardResponse> cards = CARD_SERVICE.getAllCardsByUser();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my card by id.", responses = {
            @ApiResponse(responseCode = "200", description = "Card returned successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> getMyCardById(
            @PathVariable Long id
    ){
        CardResponse card = CARD_SERVICE.getCardById(id);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new card.", responses = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> createNewCard(@Valid @ModelAttribute CardRequest cardRequest) throws IOException {
        CardResponse newCard = CARD_SERVICE.createCard(cardRequest);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);

    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "UpdateCard card.", responses = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> updateCardById(
            @PathVariable Long id,
            @Valid @ModelAttribute CardRequest cardRequest
    ){
        CardResponse updatedCard = CARD_SERVICE.updateCard(id, cardRequest);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card.", responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/CardNotFound"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCardById( @PathVariable Long id){
        CARD_SERVICE.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
