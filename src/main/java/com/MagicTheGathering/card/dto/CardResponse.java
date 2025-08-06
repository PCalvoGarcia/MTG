package com.MagicTheGathering.card.dto;

import java.time.LocalDateTime;


public record CardResponse(
        Long id,
        LocalDateTime createdAt,
        String name,
        java.util.Set<String> cardType,
        String specificType,
        int manaTotalCost,
        java.util.Set<String> manaColor,
        String textRules,
        int power,
        int endurance,
        int loyalty,
        String collection,
        int cart_number,
        String artist,
        String edition,
        String imageUrl,
        java.util.Set<String> legality,
        int quantity,
        Long userId
        ) {
}
