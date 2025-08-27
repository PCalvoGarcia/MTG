package com.MagicTheGathering.like;

import com.MagicTheGathering.like.dto.DeckLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/deck/{id}/like")
public class DeckLikeController {
    private final DeckLikeService deckLikeService;

    @PostMapping
    public ResponseEntity<DeckLikeResponse> manageLike(@PathVariable("id") Long deckId) {
        return new ResponseEntity<>(deckLikeService.manageLike(deckId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<DeckLikeResponse> getLikesByDeck(@PathVariable("id") Long deckId){
        return new ResponseEntity<>(deckLikeService.getLikesByDeckId(deckId), HttpStatus.OK);
    }

}
