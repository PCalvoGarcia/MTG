package com.MagicTheGathering.card.dto;

import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record CardRequest(

        @NotBlank(message = "Name cannot be blank") @Size(min = 2,max = 50, message = "Name cannot be longer than 50 characters!")
        String name,

        @NotNull(message = "Type cannot be null")
        CardType cardType,

        String specificType,

        @PositiveOrZero
        @Max(16)
        int manaTotalCost,

        @NotNull(message = "Mana color cannot be null")
        Set<ManaColor> manaColor,

        String textRules,

        @PositiveOrZero
        @Max(20)
        int power,

        @Positive
        @Max(20)
        int endurance,

        int loyalty,

        String collection,

        int cardNumber,

        String artist,

        String edition,

        @NotNull
        MultipartFile image,

        @NotNull(message = "Legality cannot be null")
        Set<Legality> legality,

        @Positive
        int quantity
) {
}
