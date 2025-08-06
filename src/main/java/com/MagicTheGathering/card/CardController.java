package com.MagicTheGathering.card;

import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;

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

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> createNewCard(@RequestBody CardRequest cardRequest){
        CardResponse newCard = CARD_SERVICE.createCard(cardRequest);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);

    }
    private int convertToZeroBasedPage(int page) {
        return page - 1;
    }
}
