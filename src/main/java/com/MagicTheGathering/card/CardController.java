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

import javax.swing.plaf.PanelUI;
import java.io.IOException;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService CARD_SERVICE;

    @GetMapping("/my-cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ){
        Page<CardResponse> cards = CARD_SERVICE.getAllCardsByUser(convertToZeroBasedPage(page), size);
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

    private int convertToZeroBasedPage(int page) {
        return page - 1;
    }
}
