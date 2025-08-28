package com.MagicTheGathering.like.dto;

import com.MagicTheGathering.deck.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckLikeMapperDto {
    public static DeckLikeResponse fromEntity(Deck deck, boolean liked, long count) {
        return DeckLikeResponse.builder()
                .deckId(deck.getId())
                .liked(liked)
                .likeCount(count)
                .build();
    }
}
