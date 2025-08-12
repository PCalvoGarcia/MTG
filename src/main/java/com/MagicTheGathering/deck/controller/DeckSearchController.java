package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckSearchService;
import com.MagicTheGathering.legality.Legality;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks/search")
@RequiredArgsConstructor
public class DeckSearchController {

    private final DeckSearchService DECK_SEARCH_SERVICE;

    @GetMapping("/by-format/{format}")
    public ResponseEntity<Page<DeckResponse>> getDecksByFormat(
            @PathVariable String format,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Pageable pageable = PageRequest.of(convertToZeroBasedPage(page), size);
        Page<DeckResponse> decks = DECK_SEARCH_SERVICE.getDecksByFormat(format, pageable);
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Page<DeckResponse>> getPublicDecksByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Pageable pageable = PageRequest.of(convertToZeroBasedPage(page), size);
        Page<DeckResponse> decks = DECK_SEARCH_SERVICE.getPublicDecksByUser(userId, pageable);
        return new ResponseEntity<>(decks, HttpStatus.OK);
    }

    private int convertToZeroBasedPage(int page) {
        return page - 1;
    }
}
