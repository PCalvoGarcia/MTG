package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.dto.DeckCardMapperDto;
import com.MagicTheGathering.deckCard.dto.DeckCardRequest;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckCardMapperDtoTest {
    @Test
    void when_fromEntity_return_null_from_nullDeckCard(){
        DeckCard deckCard = null;

        assertNull(DeckCardMapperDto.fromEntity(deckCard));
    }

    @Test
    void when_toEntity_return_null_from_null() {
        DeckCardRequest deckCardRequest = null;
        Card card = null;
        Deck deck = null;
        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        Deck testDeck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(testUser)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        assertNull(DeckCardMapperDto.toEntity(deckCardRequest,new Card(),testDeck));
        assertNull(DeckCardMapperDto.toEntity(new DeckCardRequest(1L, 1L, 1), card,testDeck));
        assertNull(DeckCardMapperDto.toEntity(new DeckCardRequest(1L, 1L, 1), new Card(),deck));
    }

    @Test
    void when_toEntity_return_DeckCard() {
        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        Deck testDeck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(testUser)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        Card testCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .manaTotalCost(1)
                .build();

        DeckCardId deckCardId = new DeckCardId(testDeck.getId(), testCard.getId());

        DeckCardRequest deckCardRequest = new DeckCardRequest(testDeck.getId(), testCard.getId(), 1);

        DeckCard expected = new DeckCard(deckCardId,testDeck,testCard,1);

        DeckCard response = DeckCardMapperDto.toEntity(deckCardRequest, testCard, testDeck);

        assertEquals(expected.getCard(), response.getCard());
        assertEquals(expected.getDeck(), response.getDeck());
        assertEquals(testDeck.getId(), deckCardRequest.deckId());
        assertEquals(testCard.getId(), deckCardRequest.cardId());
        assertEquals(1, deckCardRequest.quantity());

    }
}
