package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.Exceptions.UnauthorizedAccessException;
import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardService;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class DeckCardServiceTest {

    @Mock
    private DeckCardRepository deckCardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserSecurityUtils userSecurityUtils;

    @Mock
    private CardService cardService;

    @InjectMocks
    private DeckCardService deckCardService;

    private Deck deck;
    private Card card;
    private DeckCard deckCard;
    private DeckCardId deckCardId;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        deck = Deck.builder().id(1L).isPublic(true).user(user).build();
        card = Card.builder().id(1L).user(user).legalityFormat(Set.of(Legality.STANDARD)).types(Set.of(CardType.INSTANT)).build();
        deckCardId = new DeckCardId(deck.getId(), card.getId());
        deckCard = DeckCard.builder().id(deckCardId).deck(deck).card(card).quantity(2).build();
    }

    @Nested
    class GetCardByDeckId{
        @Test
        void getCardsByDeckId_should_Return_DeckCards_when_DeckIsPublic() {
            deck.setDeckCards(Set.of(deckCard));
            when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
            when(userService.getAuthenticatedUser()).thenReturn(user);

            List<DeckCardResponse> result = deckCardService.getCardsByDeckId(deck.getId());

            assertEquals(1, result.size());
        }

        @Test
        void getCardsByDeckId_should_Throw_when_DeckIsPrivateAndUnauthorized() {
            deck.setIsPublic(false);
            when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(false);

            assertThrows(AccessDeniedPrivateDeckException.class, () -> {
                deckCardService.getCardsByDeckId(deck.getId());
            });
        }
    }

    @Nested
    class GetDeckCard{
        @Test
        void getDeckCard_should_Return_DeckCard_when_Authorized() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(true);

            DeckCardResponse response = deckCardService.getDeckCard(deck.getId(), card.getId());

            assertNotNull(response);
        }

        @Test
        void getDeckCard_should_Throw_UnauthorizedAccess_when_NotAuthorizedAndPrivate() {
            deck.setIsPublic(false);
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(false);

            assertThrows(UnauthorizedAccessException.class, () -> {
                deckCardService.getDeckCard(deck.getId(), card.getId());
            });
        }

    }

    @Nested
    class UpdateDeckCardQuantity{
        @Test
        void updateDeckCardQuantity_should_UpdateQuantity() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(true);
            when(deckCardRepository.save(any())).thenReturn(deckCard);
            when(cardService.getCardById(card.getId())).thenReturn(CardMapperDto.fromEntity(card));

            DeckCardResponse response = deckCardService.updateDeckCardQuantity(deck.getId(), card.getId(), 3);

            assertEquals(3, deckCard.getQuantity());
            assertNotNull(response);
        }

        @Test
        void updateDeckCardQuantity_should_DeleteCard_when_QuantityIsZeroOrLess() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(true);

            DeckCardResponse response = deckCardService.updateDeckCardQuantity(deck.getId(), card.getId(), 0);

            verify(deckCardRepository).delete(deckCard);
            assertNull(response);
        }

        @Test
        void updateDeckCardQuantity_should_UpdateQuantity_when_TypeISBasicLand() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(true);
            when(cardService.getCardById(card.getId())).thenReturn(CardMapperDto.fromEntity(card));

            assertThrows(MaxCopiesAllowedException.class, () -> {
                deckCardService.updateDeckCardQuantity(deck.getId(), card.getId(), 5);
            });
        }

        @Test
        void updateDeckCardQuantity_should_Throw_when_QuantityExceedsLimit() {
            card = Card.builder().id(1L).user(user).types(new HashSet<>(Set.of(CardType.BASIC_LAND))).legalityFormat(Set.of(Legality.STANDARD)).types(Set.of(CardType.INSTANT)).build();
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(false);
            when(cardService.getCardById(card.getId())).thenReturn(CardMapperDto.fromEntity(card));

            assertThrows(UnauthorizedModificationsException.class, () -> {
                deckCardService.updateDeckCardQuantity(deck.getId(), card.getId(), 5);
            });
        }

    }

    @Nested
    class RemoveDeckCard{
        @Test
        void removeDeckCard_should_Delete_when_Authorized() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(true);

            deckCardService.removeDeckCard(deck.getId(), card.getId());

            verify(deckCardRepository).delete(deckCard);
        }

        @Test
        void removeDeckCard_should_Throw_when_Unauthorized() {
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(userService.getAuthenticatedUser()).thenReturn(user);
            when(userSecurityUtils.isAuthorizedToModifyDeck(deck)).thenReturn(false);

            assertThrows(UnauthorizedModificationsException.class, () -> {
                deckCardService.removeDeckCard(deck.getId(), card.getId());
            });
        }

    }

    @Test
    void getExistingDeckCard_should_ReturnNullI_fNotExists() {
        when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.empty());

        DeckCard result = deckCardService.getExistingDeckCard(deckCardId);

        assertNull(result);
    }
}
