package com.MagicTheGathering.like.dto;

import lombok.Builder;

@Builder
public record DeckLikeResponse(
        Long deckId,
        boolean liked,
        long likeCount
) {
}
