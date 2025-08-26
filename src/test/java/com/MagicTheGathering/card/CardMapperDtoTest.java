package com.MagicTheGathering.card;

import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;


import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CardMapperDtoTest {

    @Test
    void when_fromEntity_return_null_from_nullCard(){
        Card card = null;

        assertNull(CardMapperDto.fromEntity(card));
    }

    @Test
    void when_fromEntityCardTypeNull_handle_gracefully(){
        Card card = new Card();
        card.setId(1L);
        card.setTypes(null);
        card.setUser(new User());

        CardResponse response = CardMapperDto.fromEntity(card);

        assertNotNull(response);
        assertNotNull(response.cardType());
    }



    @Test
    void when_fromEntityLegalityNull_handle_gracefully(){
        Card card = new Card();
        card.setId(1L);
        card.setLegalityFormat(null);
        card.setUser(new User());

        CardResponse response = CardMapperDto.fromEntity(card);

        assertNotNull(response);
        assertNotNull(response.legality());
    }

    @Test
    void when_toEntity_return_null_from_nullCard(){
        CardRequest cardRequest = null;

        assertNull(CardMapperDto.toEntity(cardRequest, "imageUrl"));
    }

    @Test
    void when_toEntityCardTypeNull_return_emptyListCardType (){
        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        CardRequest cardRequest = new CardRequest(
                "Lightning Bolt",
                null,
                "Instant",
                1,
                Set.of(ManaColor.RED),
                "Lightning Bolt deals 3 damage to any target.",
                0,
                0,
                0,
                "Core Set 2021",
                137,
                "Christopher Rush",
                "M21",
                mockFile,
                Set.of(Legality.STANDARD),
                4
        );

        assertEquals(new HashSet<>(), CardMapperDto.toEntity(cardRequest, "imageUrl").getTypes());

    }
}
