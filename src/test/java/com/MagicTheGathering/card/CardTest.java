package com.MagicTheGathering.card;

import com.MagicTheGathering.deck.Deck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CardTest {

    @Test
    void onCreate() {
        Card card = new Card();

        card.onCreate();

        assertNotNull(card.getCreatedAt());
    }

    @Test
    void equals_return_true_from_sameCard() {
        Card card = Card.builder().id(1L).build();

        assertEquals(card, card);
    }

    @Test
    void equals_return_true_from_equalCards() {
        Card card1 = Card.builder().id(1L).build();
        Card card2 = Card.builder().id(1L).build();

        assertEquals(card1, card2);
    }

    @Test
    void equals_return_false_from_nullCard() {
        Card card = Card.builder().id(1L).build();

        assertNotEquals(card, null);
    }

    @Test
    void equals_return_false_from_differentClass() {
        Card card = Card.builder().id(1L).build();
        Deck deck = Deck.builder().id(1L).build();

        assertNotEquals(card, deck);
    }

    @Test
    void testHashCode() {
        Card card1 = Card.builder().id(1L).build();
        Card card2 = Card.builder().id(1L).build();

        assertEquals(card1.hashCode(), card2.hashCode());
    }
}