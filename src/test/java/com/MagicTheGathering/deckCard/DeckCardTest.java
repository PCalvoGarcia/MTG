package com.MagicTheGathering.deckCard;


import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCartId.DeckCardId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckCardTest {

    @Test
    void equals_return_true_from_sameDeckCard() {
       DeckCard  deckCard = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();

        assertEquals( deckCard,  deckCard);
    }

    @Test
    void equals_return_true_from_equalDeckCard() {
       DeckCard  deckCard1 = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();
       DeckCard  deckCard2 = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();

        assertEquals( deckCard1,  deckCard2);
    }

    @Test
    void equals_return_false_from_nullDeckCard() {
       DeckCard  deckCard = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();

        assertNotEquals( deckCard, null);
    }

    @Test
    void equals_return_false_from_differentClass() {
        DeckCard  deckCard = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();
        Deck deck = Deck.builder().id(1L).build();

        assertNotEquals( deckCard, deck);
    }

    @Test
    void testHashCode() {
       DeckCard  deckCard1 = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();
       DeckCard  deckCard2 = DeckCard.builder().id(new DeckCardId(1L, 1L)).build();

        assertEquals( deckCard1.hashCode(),  deckCard2.hashCode());
    }
}
