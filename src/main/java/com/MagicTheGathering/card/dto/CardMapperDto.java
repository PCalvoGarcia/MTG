package com.MagicTheGathering.card.dto;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CardMapperDto {
    public static CardResponse fromEntity(Card card){

        if (card == null) {
            return null;
        }

        java.util.Set<String> cardType = card.getTypes() == null ? java.util.Collections.emptySet()
                : card.getTypes()
                .stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet());

        java.util.Set<String> manaColor = card.getManaColors() == null ? java.util.Collections.emptySet()
                : card.getManaColors()
                .stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet());

        java.util.Set<String> legality = card.getLegalityFormat() == null ? java.util.Collections.emptySet()
                : card.getLegalityFormat()
                .stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet());

        return new CardResponse(
                card.getId(),
                card.getCreatedAt(),
                card.getName(),
                cardType,
                card.getSpecificType(),
                card.getManaTotalCost(),
                manaColor,
                card.getTextRules(),
                card.getPower(),
                card.getEndurance(),
                card.getLoyalty(),
                card.getCollection(),
                card.getCartNumber(),
                card.getArtist(),
                card.getEdition(),
                card.getImageUrl(),
                legality,
                card.getQuantity(),
                card.getUser().getId());
    }

    public static Card toEntity(CardRequest cardRequest, String imageUrl){

        if (cardRequest == null) {
            return null;
        }

        Set<CardType> cardTypes = new HashSet<>();
        if (cardRequest.cardType() != null) {
            cardTypes.add(cardRequest.cardType());
        }

        Set<ManaColor> manaColors = new HashSet<>();
        if (cardRequest.manaColor() != null) {
            manaColors.add(cardRequest.manaColor());
        }


        Set<Legality> legalities = new HashSet<>();
        if (cardRequest.legality() != null) {
            legalities.add(cardRequest.legality());
        }



        return Card.builder()
                .name(cardRequest.name())
                .types(cardTypes)
                .specificType(cardRequest.specificType())
                .manaTotalCost(cardRequest.manaTotalCost())
                .manaColors(manaColors)
                .textRules(cardRequest.textRules())
                .power(cardRequest.power())
                .endurance(cardRequest.endurance())
                .loyalty(cardRequest.loyalty())
                .collection(cardRequest.collection())
                .cartNumber(cardRequest.cart_number())
                .artist(cardRequest.artist())
                .edition(cardRequest.edition())
                .imageUrl(imageUrl)
                .legalityFormat(legalities)
                .quantity(cardRequest.quantity())
                .build();
    }
}
