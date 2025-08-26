package com.MagicTheGathering.deck;


import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.dto.DeckMapperDto;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckMapperDtoTest {

    private Deck deck;
    private DeckCard deckCard1;
    private DeckCard deckCard2;
    private Card card1;
    private Card card2;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).build();

        deck = Deck.builder()
                .id(1L)
                .deckName("My Test Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(user)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        card1 = Card.builder()
                .id(10L)
                .createdAt(LocalDateTime.now())
                .name("Lightning Bolt")
                .types(new HashSet<>(Set.of(CardType.INSTANT)))
                .manaTotalCost(1)
                .manaColors(new HashSet<>(Set.of(ManaColor.RED)))
                .textRules("Deals 3 damage...")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Core Set")
                .cardNumber(1)
                .artist("Artist A")
                .edition("M21")
                .imageUrl("url1")
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD)))
                .quantity(4)
                .user(user)
                .build();

        card2 = Card.builder()
                .id(11L)
                .createdAt(LocalDateTime.now())
                .name("Plains")
                .types(new HashSet<>(Set.of(CardType.BASIC_LAND)))
                .manaTotalCost(0)
                .manaColors(new HashSet<>(Set.of(ManaColor.WHITE)))
                .textRules("Add {W}.")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Core Set")
                .cardNumber(2)
                .artist("Artist B")
                .edition("M21")
                .imageUrl("url2")
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD)))
                .quantity(20)
                .user(user)
                .build();

        DeckCardId id1 = new DeckCardId();
        id1.setDeckId(deck.getId());
        id1.setCardId(card1.getId());

        deckCard1 = new DeckCard();
        deckCard1.setId(id1);
        deckCard1.setCard(card1);
        deckCard1.setQuantity(4);
        deckCard1.setDeck(deck);

        DeckCardId id2 = new DeckCardId();
        id2.setDeckId(deck.getId());
        id2.setCardId(card2.getId());

        deckCard2 = new DeckCard();
        deckCard1.setId(id2);
        deckCard2.setCard(card2);
        deckCard2.setQuantity(20);
        deckCard2.setDeck(deck);

        deck.getDeckCards().add(deckCard1);
        deck.getDeckCards().add(deckCard2);

    }

    @Test
    void when_fromEntity_return_null_from_nullDeck(){
        Deck deck = null;

        assertNull(DeckMapperDto.fromEntity(deck));
    }

    @Test
    void fromEntity_should_mapDeckToResponseNotNull() {
        DeckResponse response = DeckMapperDto.fromEntity(deck);

        assertNotNull(response);
        assertEquals(deck.getId(), response.id());
        assertEquals(deck.getDeckName(), response.deckName());
        assertEquals(deck.getIsPublic(), response.isPublic());
        assertEquals(deck.getType().name(), response.type());
        assertEquals(deck.getMaxCards(), response.maxCards());
        assertEquals(24, response.totalCards());
        assertEquals(deck.getUser().getId(), response.userId());
        assertEquals(2, response.cards().size());

        DeckCardResponse firstCard = response.cards().get(0);
        DeckCardResponse secondCard = response.cards().get(1);

        DeckCardResponse creature = firstCard.card().name().equals("Lightning Bolt") ? firstCard : secondCard;
        DeckCardResponse land = firstCard.card().name().equals("Plains") ? firstCard : secondCard;

        assertEquals(4, creature.quantity());
        assertEquals("Lightning Bolt", creature.card().name());
        assertTrue(creature.card().cardType().contains("INSTANT"));
        assertTrue(creature.card().manaColor().contains("RED"));

        assertEquals(20, land.quantity());
        assertEquals("Plains", land.card().name());
        assertTrue(land.card().cardType().contains("BASIC_LAND"));
        assertTrue(land.card().manaColor().contains("WHITE"));

    }

    @Test
    void fromEntity_should_mapDeckToResponseWithNull() {
        deck.setDeckCards(new HashSet<>());

        card1.setTypes(null);
        card1.setManaColors(null);
        card1.setLegalityFormat(null);
        card1.setUser(null);

        DeckCardId id1 = new DeckCardId();
        id1.setDeckId(deck.getId());
        id1.setCardId(card1.getId());

        deckCard1 = new DeckCard();
        deckCard1.setId(id1);
        deckCard1.setCard(card1);
        deckCard1.setQuantity(4);
        deckCard1.setDeck(deck);

        card2.setTypes(null);
        card2.setManaColors(null);
        card2.setLegalityFormat(null);
        card2.setUser(null);
        DeckCardId id2 = new DeckCardId();
        id2.setDeckId(deck.getId());
        id2.setCardId(card2.getId());

        deckCard2 = new DeckCard();
        deckCard1.setId(id2);
        deckCard2.setCard(card2);
        deckCard2.setQuantity(20);
        deckCard2.setDeck(deck);

        deck.getDeckCards().add(deckCard1);
        deck.getDeckCards().add(deckCard2);

        DeckResponse response = DeckMapperDto.fromEntity(deck);

        assertNotNull(response);
        assertEquals(deck.getId(), response.id());
        assertEquals(deck.getDeckName(), response.deckName());
        assertEquals(deck.getIsPublic(), response.isPublic());
        assertEquals(deck.getType().name(), response.type());
        assertEquals(deck.getMaxCards(), response.maxCards());
        assertEquals(24, response.totalCards());
        assertEquals(deck.getUser().getId(), response.userId());
        assertEquals(2, response.cards().size());

        DeckCardResponse firstCard = response.cards().get(0);
        DeckCardResponse secondCard = response.cards().get(1);

        DeckCardResponse creature = firstCard.card().name().equals("Lightning Bolt") ? firstCard : secondCard;
        DeckCardResponse land = firstCard.card().name().equals("Plains") ? firstCard : secondCard;

        assertEquals(4, creature.quantity());
        assertEquals("Lightning Bolt", creature.card().name());
        assertEquals(new HashSet<>(), creature.card().cardType());
        assertEquals(new HashSet<>(), creature.card().manaColor());

        assertEquals(20, land.quantity());
        assertEquals("Plains", land.card().name());
        assertEquals(new HashSet<>(), land.card().cardType());
        assertEquals(new HashSet<>(), land.card().manaColor());

        deck.setUser(null);
        response = DeckMapperDto.fromEntity(deck);

        assertEquals(null, response.userId());
    }

    @Test
    void when_toEntity_return_null_from_nullDeck(){
        DeckRequest deckRequest = null;
        assertNull(DeckMapperDto.toEntity(deckRequest, new User()));
    }

}
