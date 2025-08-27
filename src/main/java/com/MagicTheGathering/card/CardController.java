package com.MagicTheGathering.card;

import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class CardController {
    private final CardService CARD_SERVICE;

    @GetMapping("/my-cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardResponse>> getMyCards(){
        List<CardResponse> cards = CARD_SERVICE.getAllCardsByUser();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> getMyCardById(
            @PathVariable Long id
    ){
        CardResponse card = CARD_SERVICE.getCardById(id);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> createNewCard(@Valid @ModelAttribute CardRequest cardRequest) throws IOException {
        CardResponse newCard = CARD_SERVICE.createCard(cardRequest);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);

    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> updateDestinationById(
            @PathVariable Long id,
            @Valid @ModelAttribute CardRequest cardRequest
    ){
        CardResponse updatedCard = CARD_SERVICE.updateCard(id, cardRequest);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCardById( @PathVariable Long id){
        CARD_SERVICE.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
