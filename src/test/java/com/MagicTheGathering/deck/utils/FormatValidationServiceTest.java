package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.exceptions.IllegalCardException;
import com.MagicTheGathering.deck.exceptions.MaxCommanderException;
import com.MagicTheGathering.deck.exceptions.MaxCopiesAllowedFormatException;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FormatValidationServiceTest {

    @InjectMocks
    private FormatValidationService formatValidationService;

    private User user;
    private Deck standardDeck;
    private Deck commanderDeck;
    private Card basicLand;
    private Card nonBasicCard;
    private Card commanderCard;
    private Card illegalCard;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();

        standardDeck = Deck.builder()
                .id(1L)
                .deckName("Standard Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(user)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        commanderDeck = Deck.builder()
                .id(2L)
                .deckName("Commander Deck")
                .isPublic(true)
                .type(Legality.COMMANDER)
                .maxCards(100)
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
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD, Legality.COMMANDER)))
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
                .legalityFormat(new HashSet<>(Set.of(Legality.STANDARD, Legality.COMMANDER)))
                .quantity(4)
                .user(user)
                .build();

        commanderCard = Card.builder()
                .id(3L)
                .name("Test Commander")
                .types(new HashSet<>(Set.of(CardType.COMMANDER)))
                .manaTotalCost(4)
                .manaColors(new HashSet<>(Set.of(ManaColor.BLUE)))
                .textRules("Commander ability...")
                .power(3)
                .endurance(3)
                .loyalty(0)
                .collection("Commander Set")
                .cardNumber(1)
                .artist("Artist C")
                .edition("CMD21")
                .imageUrl("url3")
                .legalityFormat(new HashSet<>(Set.of(Legality.COMMANDER)))
                .quantity(1)
                .user(user)
                .build();

        illegalCard = Card.builder()
                .id(4L)
                .name("Illegal Card")
                .types(new HashSet<>(Set.of(CardType.INSTANT)))
                .manaTotalCost(1)
                .manaColors(new HashSet<>(Set.of(ManaColor.BLACK)))
                .textRules("Not legal in standard...")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Legacy Set")
                .cardNumber(1)
                .artist("Artist D")
                .edition("LEG21")
                .imageUrl("url4")
                .legalityFormat(new HashSet<>(Set.of(Legality.LEGACY_VINTAGE)))
                .quantity(4)
                .user(user)
                .build();
    }

    @Nested
    class StandardFormat {
        @Test
        void validateCardAddition_shouldAllowLegalCardInStandard() {
            assertDoesNotThrow(() -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 4));
        }

        @Test
        void validateCardAddition_shouldThrowExceptionForIllegalCardInStandard() {
            IllegalCardException exception = assertThrows(
                    IllegalCardException.class,
                    () -> formatValidationService.validateCardAddition(standardDeck, illegalCard, 1)
            );

            assertNotNull(exception);
        }

        @Test
        void validateCardAddition_shouldThrowExceptionForMoreThan4NonBasicCardsInStandard() {
            MaxCopiesAllowedException exception = assertThrows(
                    MaxCopiesAllowedException.class,
                    () -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 5)
            );

            assertNotNull(exception);
        }

        @Test
        void validateCardAddition_shouldAllowMoreThan4BasicLandsInStandard() {
            assertDoesNotThrow(() -> formatValidationService.validateCardAddition(standardDeck, basicLand, 10));
        }
    }

    @Nested
    class CommanderFormat {
        @Test
        void validateCardAddition_shouldAllowSingleCopyInCommander() {
            assertDoesNotThrow(() -> formatValidationService.validateCardAddition(commanderDeck, nonBasicCard, 1));
        }

        @Test
        void validateCardAddition_shouldThrowExceptionForMultipleCopiesInCommander() {
            MaxCopiesAllowedFormatException exception = assertThrows(
                    MaxCopiesAllowedFormatException.class,
                    () -> formatValidationService.validateCardAddition(commanderDeck, nonBasicCard, 2)
            );

            assertNotNull(exception);
        }

        @Test
        void validateCardAddition_shouldAllowMultipleBasicLandsInCommander() {
            assertDoesNotThrow(() -> formatValidationService.validateCardAddition(commanderDeck, basicLand, 5));
        }

        @Test
        void validateCardAddition_shouldAllowSingleCommanderCard() {
            MaxCommanderException exception = assertThrows(MaxCommanderException.class, () -> formatValidationService.validateCardAddition(commanderDeck, commanderCard, 2));

            assertEquals(new MaxCommanderException().getMessage(), exception.getMessage());
        }

    }

    @Test
    void validateCardAddition_shouldThrowWhenIllegalCardAddedToFullStandardDeck() {
        for (int i = 0; i < 15; i++) {
            DeckCard deckCard = new DeckCard();
            deckCard.setCard(basicLand);
            deckCard.setQuantity(4);
            deckCard.setDeck(standardDeck);
            standardDeck.getDeckCards().add(deckCard);
        }

        nonBasicCard.setLegalityFormat(new HashSet<>(Set.of(Legality.LEGACY_VINTAGE)));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 1)
        );

        assertEquals(new IllegalCardException(nonBasicCard.getName(), standardDeck.getType()).getMessage(), exception.getMessage());
    }

    @Test
    void validateCardAddition_shouldThrowWhenCardAddedToFullStandardDeck() {
            DeckCard deckCard = new DeckCard();
            deckCard.setCard(basicLand);
            deckCard.setQuantity(150);
            deckCard.setDeck(standardDeck);
            standardDeck.getDeckCards().add(deckCard);



        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 1)
        );

        assertEquals(new RuntimeException("Adding " + 1 + " cards would exceed maximum deck size of " + standardDeck.getMaxCards() + " for " + standardDeck.getType()).getMessage(), exception.getMessage());
    }


    @Test
    void validateCardAddition_shouldConsiderExistingCardsWhenValidatingLimits() {
        DeckCard existingDeckCard = new DeckCard();
        existingDeckCard.setCard(nonBasicCard);
        existingDeckCard.setQuantity(3);
        existingDeckCard.setDeck(standardDeck);
        standardDeck.getDeckCards().add(existingDeckCard);

        assertDoesNotThrow(() -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 1));

        MaxCopiesAllowedException exception = assertThrows(
                MaxCopiesAllowedException.class,
                () -> formatValidationService.validateCardAddition(standardDeck, nonBasicCard, 2)
        );

        assertNotNull(exception);
    }

    @Test
    void isCommander_return_false(){

    }
}