package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DeckServiceHelperTest {

    @Mock
    private FormatValidationService formatValidationService;

    @InjectMocks
    private DeckServiceHelper deckServiceHelper;

    private User user;
    private Deck deck;
    private Card basicLand;
    private Card nonBasicCard;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();

        deck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(user)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        basicLand = Card.builder()
                .id(1L)
                .name("Plains")
                .types(new HashSet<>(Set.of(CardType.BASIC_LAND)))
                .manaTotalCost(0)
                .manaColors(new HashSet<>(Set.of(ManaColor.WHITE)))
                .textRules("Add {W}.")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Core Set")
                .cardNumber(1)
                .artist("Artist A")
                .edition("M21")
                .imageUrl("url1")
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD)))
                .quantity(20)
                .user(user)
                .build();

        nonBasicCard = Card.builder()
                .id(2L)
                .name("Lightning Bolt")
                .types(new HashSet<>(Set.of(CardType.INSTANT)))
                .manaTotalCost(1)
                .manaColors(new HashSet<>(Set.of(ManaColor.RED)))
                .textRules("Deals 3 damage...")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Core Set")
                .cardNumber(2)
                .artist("Artist B")
                .edition("M21")
                .imageUrl("url2")
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD)))
                .quantity(4)
                .user(user)
                .build();
    }

    @Test
    void validateCardAddition_shouldCallFormatValidationService() {
        int quantity = 2;

        deckServiceHelper.validateCardAddition(deck, nonBasicCard, quantity);

        verify(formatValidationService).validateCardAddition(deck, nonBasicCard, quantity);
    }

    @Test
    void validateMaxCopiesLand_shouldAllowMoreThan4BasicLands() {
        assertDoesNotThrow(() -> deckServiceHelper.validateMaxCopiesLand(basicLand, 10));
    }

    @Test
    void validateMaxCopiesLand_shouldAllowUpTo4NonBasicCards() {
        assertDoesNotThrow(() -> deckServiceHelper.validateMaxCopiesLand(nonBasicCard, 4));
    }

    @Test
    void validateMaxCopiesLand_shouldThrowExceptionForMoreThan4NonBasicCards() {
        MaxCopiesAllowedException exception = assertThrows(
                MaxCopiesAllowedException.class,
                () -> deckServiceHelper.validateMaxCopiesLand(nonBasicCard, 5)
        );

        assertNotNull(exception);
    }

    @Test
    void validateMaxCopiesLand_shouldAllowExactly4NonBasicCards() {
        assertDoesNotThrow(() -> deckServiceHelper.validateMaxCopiesLand(nonBasicCard, 4));
    }
}
