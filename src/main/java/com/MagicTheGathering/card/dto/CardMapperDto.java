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

    public static Card toEntity(CardResponse cardResponse){

        if (cardResponse == null) {
            return null;
        }

        Set<CardType> cardTypes = new HashSet<>();
        if (cardResponse.cardType() != null) {
            cardTypes = cardResponse.cardType().stream()
                    .map(CardType::valueOf)
                    .collect(Collectors.toSet());
        }

        Set<ManaColor> manaColors = new HashSet<>();
        if (cardResponse.manaColor() != null) {
            manaColors = cardResponse.manaColor().stream()
                    .map(ManaColor::valueOf)
                    .collect(Collectors.toSet());
        }


        Set<Legality> legalities = new HashSet<>();
        if (cardResponse.legality() != null) {
            legalities = cardResponse.legality().stream()
                    .map(Legality::valueOf)
                    .collect(Collectors.toSet());
        }

        User user = new User();
        user.setId(cardResponse.userId());


        return Card.builder()
                .id(cardResponse.id())
                .createdAt(cardResponse.createdAt())
                .name(cardResponse.name())
                .types(cardTypes)
                .specificType(cardResponse.specificType())
                .manaTotalCost(cardResponse.manaTotalCost())
                .manaColors(manaColors)
                .textRules(cardResponse.textRules())
                .power(cardResponse.power())
                .endurance(cardResponse.endurance())
                .loyalty(cardResponse.loyalty())
                .collection(cardResponse.collection())
                .cartNumber(cardResponse.cart_number())
                .artist(cardResponse.artist())
                .edition(cardResponse.edition())
                .imageUrl(cardResponse.imageUrl())
                .legalityFormat(legalities)
                .quantity(cardResponse.quantity())
                .user(user)
                .build();
    }
}
