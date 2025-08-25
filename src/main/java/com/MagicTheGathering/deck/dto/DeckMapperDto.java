package com.MagicTheGathering.deck.dto;

import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class DeckMapperDto {

    public static DeckResponse fromEntity(Deck deck){
        if (deck == null){
            return null;
        }

        List<DeckCardResponse> cards = deck.getDeckCards()
                .stream()
                .map(deckCard -> new DeckCardResponse(
                        new CardResponse(
                                deckCard.getCard().getId(),
                                deckCard.getCard().getCreatedAt(),
                                deckCard.getCard().getName(),
                                deckCard.getCard().getTypes() == null ? java.util.Collections.emptySet()
                                        : deckCard.getCard().getTypes()
                                        .stream()
                                        .map(Enum::name)
                                        .collect(java.util.stream.Collectors.toSet()),
                                deckCard.getCard().getSpecificType(),
                                deckCard.getCard().getManaTotalCost(),
                                deckCard.getCard().getManaColors() == null ? java.util.Collections.emptySet()
                                        : deckCard.getCard().getManaColors()
                                        .stream()
                                        .map(Enum::name)
                                        .collect(java.util.stream.Collectors.toSet()),
                                deckCard.getCard().getTextRules(),
                                deckCard.getCard().getPower(),
                                deckCard.getCard().getEndurance(),
                                deckCard.getCard().getLoyalty(),
                                deckCard.getCard().getCollection(),
                                deckCard.getCard().getCardNumber(),
                                deckCard.getCard().getArtist(),
                                deckCard.getCard().getEdition(),
                                deckCard.getCard().getImageUrl(),
                                deckCard.getCard().getLegalityFormat() == null ? java.util.Collections.emptySet()
                                : deckCard.getCard().getLegalityFormat()
                                .stream()
                                .map(Enum::name)
                                .collect(java.util.stream.Collectors.toSet()),
                                deckCard.getQuantity(),
                                deckCard.getCard().getUser() != null ? deckCard.getCard().getUser().getId() : null),
                                deckCard.getQuantity()
                ))
                .collect(Collectors.toList());

        int totalCards = cards.stream().mapToInt(DeckCardResponse::quantity).sum();

        return new DeckResponse(
                deck.getId(),
                deck.getDeckName(),
                deck.getIsPublic(),
                deck.getType().name(),
                deck.getMaxCards(),
                totalCards,
                deck.getUser() != null ? deck.getUser().getId() : null,
                cards
        );
    }

    public static Deck toEntity(DeckRequest request, User user) {
        if (request == null) {
            return null;
        }

        return Deck.builder()
                .deckName(request.deckName())
                .isPublic(request.isPublic())
                .type(Legality.valueOf(request.legalityEnum().name())) // el enum Legality
                .maxCards(request.legalityEnum().getMaxCards())
                .user(user)
                .build();
    }
}
