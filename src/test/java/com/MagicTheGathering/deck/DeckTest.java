package com.MagicTheGathering.deck;

import com.MagicTheGathering.card.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckTest {
    @Test
    void onCreate() {
        Deck deck = new Deck();

        deck.onCreate();

        assertNotNull(deck.getCreatedAt());
    }

    @Test
    void equals_return_true_from_sameDeck() {
        Deck deck = Deck.builder().id(1L).build();

        assertEquals(deck, deck);
    }

    @Test
    void equals_return_true_from_equalDecks() {
        Deck deck1 = Deck.builder().id(1L).build();
        Deck deck2 = Deck.builder().id(1L).build();

        assertEquals(deck1, deck2);
    }

    @Test
    void equals_return_false_from_nullDeck() {
        Deck deck = Deck.builder().id(1L).build();

        assertNotEquals(deck, null);
    }

    @Test
    void equals_return_false_from_differentClass() {
        Card card = Card.builder().id(1L).build();
        Deck deck = Deck.builder().id(1L).build();

        assertNotEquals(card, deck);
    }

    @Test
    void testHashCode() {
        Deck deck1 = Deck.builder().id(1L).build();
        Deck deck2 = Deck.builder().id(1L).build();

        assertEquals(deck1.hashCode(), deck2.hashCode());
    }
}
