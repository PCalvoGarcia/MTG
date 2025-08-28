package com.MagicTheGathering.deck;

import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckServiceHelper;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCard.DeckCardRepository;
import com.MagicTheGathering.deckCard.DeckCardService;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.Exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.like.DeckLike;
import com.MagicTheGathering.like.DeckLikeService;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private DeckCardRepository deckCardRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserSecurityUtils userSecurityUtils;

    @Mock
    private DeckServiceHelper deckServiceHelper;

    @Mock
    private DeckCardService deckCardService;

    @Mock
    private DeckLikeService deckLikeService;

    @InjectMocks
    private DeckService deckService;

    private User testUser;
    private Deck testDeck;
    private Card testCard;
    private DeckRequest deckRequest;
    private DeckLike testDeckLike;


    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testDeck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(testUser)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        testCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .manaTotalCost(1)
                .build();

        testDeckLike = DeckLike.builder()
                .id(1L)
                .user(testUser)
                .deck(testDeck)
                .build();

        deckRequest = new DeckRequest(
                "New Deck",
                true,
                Legality.STANDARD
        );
    }

    @Test
    void getAllDeckByUser_ShouldReturnUserDecks() {
        List<Deck> deckPage = List.of(testDeck);

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findByUser(testUser)).thenReturn(deckPage);

        List<DeckResponse> result = deckService.getAllDeckByUser();

        assertThat(result).isNotNull();
        assertEquals(1,result.size());
        assertThat(result.get(0).deckName()).isEqualTo("Test Deck");

        verify(userService).getAuthenticatedUser();
        verify(deckRepository).findByUser(testUser);
    }

    @Test
    void getAllPublicDecks_ShouldReturnPublicDecks() {
        List<Deck> deckPage = List.of(testDeck);

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findByIsPublicTrue()).thenReturn(deckPage);

        List<DeckResponse> result = deckService.getAllPublicDecks();

        assertThat(result).isNotNull();
        assertEquals(1, result.size());
        assertThat(result.get(0).isPublic()).isTrue();

        verify(deckRepository).findByIsPublicTrue();
    }


    @Nested
    class GetDeckById {

        @Test
        void getDeckById_WhenAuthorized_ShouldReturnDeck() {
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);

            DeckResponse result = deckService.getDeckById(1L);

            assertThat(result).isNotNull();
            assertThat(result.deckName()).isEqualTo("Test Deck");
            assertThat(result.id()).isEqualTo(1L);

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void getDeckById_WhenNotFound_ShouldThrowException() {
            when(deckRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deckService.getDeckById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Deck not found");

            verify(deckRepository).findById(1L);
        }

        @Test
        void getDeckById_WhenNotAuthorizedForPrivateDeck_ShouldThrowException() {
            testDeck.setIsPublic(false);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            assertThatThrownBy(() -> deckService.getDeckById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Unauthorized");

            verify(deckRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

        @Test
        void getDeckById_WhenNotPublicButUserIsAuthorized_ShouldReturnDeck() {
            testDeck.setIsPublic(false);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);

            DeckResponse result = deckService.getDeckById(1L);

            assertThat(result).isNotNull();
            assertThat(result.deckName()).isEqualTo("Test Deck");
            assertThat(result.id()).isEqualTo(1L);
            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

    }

    @Nested
    class GetDeckObjById {

        @Test
        void getDeckObjById_WhenDeckExistsAndIsPublic_ShouldReturnDeck() {
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);

            Deck result = deckService.getDeckObjById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDeckName()).isEqualTo("Test Deck");

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void getDeckObjById_WhenDeckNotFound_ShouldThrowRuntimeException() {
            when(deckRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                deckService.getDeckObjById(1L);
            });

            verify(deckRepository).findById(1L);
        }

        @Test
        void getDeckObjById_WhenDeckIsPrivateAndUserIsOwner_ShouldReturnDeck() {
            testDeck.setIsPublic(false);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);

            Deck result = deckService.getDeckObjById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

        @Test
        void getDeckObjById_WhenDeckIsPrivateAndUserIsNotOwner_ShouldThrowRuntimeException() {
            testDeck.setIsPublic(false);
            User otherUser = User.builder().id(2L).username("otheruser").build();

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(otherUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            assertThrows(RuntimeException.class, () -> {
                deckService.getDeckObjById(1L);
            });

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

        @Test
        void getDeckObjById_WhenDeckIsPublicAndUserIsNotOwner_ShouldReturnDeck() {
            User otherUser = User.builder().id(2L).username("otheruser").build();

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(otherUser);

            Deck result = deckService.getDeckObjById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
            verify(userSecurityUtils, never()).isAuthorizedToModifyDeck(any());
        }
    }

    @Nested
    class GetLikedDecksByUser {

        @Test
        void getLikedDecksByUser_WhenUserHasLikedDecks_ShouldReturnDeckResponses() {
            List<DeckLike> likedDecks = List.of(testDeckLike);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeService.getDeckLikedByUser(testUser)).thenReturn(likedDecks);

            List<DeckResponse> result = deckService.getLikedDecksByUser();

            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).deckName()).isEqualTo("Test Deck");

            verify(userService).getAuthenticatedUser();
            verify(deckLikeService).getDeckLikedByUser(testUser);
        }

        @Test
        void getLikedDecksByUser_WhenUserHasNoLikedDecks_ShouldReturnEmptyList() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeService.getDeckLikedByUser(testUser)).thenReturn(List.of());

            List<DeckResponse> result = deckService.getLikedDecksByUser();

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(userService).getAuthenticatedUser();
            verify(deckLikeService).getDeckLikedByUser(testUser);
        }

        @Test
        void getLikedDecksByUser_WhenUserHasMultipleLikedDecks_ShouldReturnAllDecks() {
            Deck deck2 = Deck.builder()
                    .id(2L)
                    .deckName("Test Deck 2")
                    .isPublic(true)
                    .user(testUser)
                    .type(Legality.STANDARD)
                    .build();

            DeckLike deckLike2 = DeckLike.builder()
                    .id(2L)
                    .user(testUser)
                    .deck(deck2)
                    .build();

            List<DeckLike> likedDecks = List.of(testDeckLike, deckLike2);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeService.getDeckLikedByUser(testUser)).thenReturn(likedDecks);

            List<DeckResponse> result = deckService.getLikedDecksByUser();

            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(1).id()).isEqualTo(2L);

            verify(userService).getAuthenticatedUser();
            verify(deckLikeService).getDeckLikedByUser(testUser);
        }

        @Test
        void getLikedDecksByUser_WhenServiceThrowsException_ShouldPropagateException() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeService.getDeckLikedByUser(testUser))
                    .thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> {
                deckService.getLikedDecksByUser();
            });

            verify(userService).getAuthenticatedUser();
            verify(deckLikeService).getDeckLikedByUser(testUser);
        }
    }

    @Test
    void createDeck_ShouldReturnCreatedDeck() {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.save(any(Deck.class))).thenReturn(testDeck);

        DeckResponse result = deckService.createDeck(deckRequest);

        assertThat(result).isNotNull();
        assertThat(result.deckName()).isEqualTo("Test Deck");

        verify(userService).getAuthenticatedUser();
        verify(deckRepository).save(any(Deck.class));
    }

    @Nested
    class UpdateDeck {

        @Test
        void updateDeck_WhenAuthorized_ShouldReturnUpdatedDeck() {
            DeckRequest updateRequest = new DeckRequest(
                    "Updated Deck",
                    false,
                    Legality.MODERN
            );

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            doNothing().when(deckLikeService).deleteLikesByDeckId(1L);

            DeckResponse result = deckService.updateDeck(1L, updateRequest);

            assertThat(result).isNotNull();
            assertThat(testDeck.getDeckName()).isEqualTo("Updated Deck");
            assertThat(testDeck.getIsPublic()).isFalse();
            assertThat(testDeck.getType()).isEqualTo(Legality.MODERN);

            verify(deckRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
            verify(deckLikeService).deleteLikesByDeckId(1L);
        }

        @Test
        void updateDeck_WhenNotAuthorized_ShouldThrowException() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            assertThatThrownBy(() -> deckService.updateDeck(1L, deckRequest))
                    .isInstanceOf(UnauthorizedModificationsException.class);

            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

        @Test
        void updateDeck_WhenDeckNotFound_ShouldThrowException() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deckService.updateDeck(999L, deckRequest))
                    .isInstanceOf(DeckIdNotFoundException.class);

            verify(deckRepository).findById(999L);
        }

        @Test
        void updateDeck_WhenChangingToPrivate_ShouldDeleteLikes() {
            testDeck.setIsPublic(true);

            DeckRequest updateRequest = new DeckRequest(
                    "Updated Deck",
                    false,
                    Legality.MODERN
            );

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            doNothing().when(deckLikeService).deleteLikesByDeckId(1L);

            deckService.updateDeck(1L, updateRequest);

            verify(deckLikeService).deleteLikesByDeckId(1L);
        }

        @Test
        void updateDeck_WhenStayingPublic_ShouldNotDeleteLikes() {
            testDeck.setIsPublic(true);

            DeckRequest updateRequest = new DeckRequest(
                    "Updated Deck",
                    true,
                    Legality.MODERN
            );

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);

            deckService.updateDeck(1L, updateRequest);

            verify(deckLikeService, never()).deleteLikesByDeckId(1L);
        }

    }

    @Nested
    class DeleteDeck {

        @Test
        void deleteDeck_WhenAuthorized_ShouldDeleteDeck() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            deckService.deleteDeck(1L);

            verify(deckRepository).findById(1L);
            verify(deckRepository).delete(testDeck);
        }

        @Test
        void deleteDeck_whenAuthorizedAndPrivate() {
            testDeck.setIsPublic(false);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);

            deckService.deleteDeck(1L);

            verify(deckRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
        }

        @Test
        void deleteDeck_whenUnauthorizedAndPrivate_should_throwException() {
            testDeck.setIsPublic(false);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> deckService.deleteDeck(1L));
            assertEquals(new UnauthorizedModificationsException().getMessage(), exception.getMessage());
        }

        @Test
        void deleteDeck_WhenDeckNotFound_ShouldThrowException() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deckService.deleteDeck(999L))
                    .isInstanceOf(DeckIdNotFoundException.class);

            verify(deckRepository).findById(999L);
        }

    }

    @Nested
    class addCardToDeck{

        @Test
        void addCardToDeck_ShouldAddCardSuccessfully() {
            AddCardDeckRequest request = new AddCardDeckRequest(1L, 2);
            DeckCardId deckCardId = new DeckCardId(1L, 1L);

            DeckCard existingDeckCard = new DeckCard();
            existingDeckCard.setId(deckCardId);
            existingDeckCard.setQuantity(2);
            existingDeckCard.setCard(testCard);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            when(userSecurityUtils.isAuthorizedToModifyDeck(any(Deck.class))).thenReturn(true);

            when(userSecurityUtils.findCardById(any(AddCardDeckRequest.class))).thenReturn(testCard);
            when(deckCardService.getExistingDeckCard(deckCardId)).thenReturn(existingDeckCard);

            DeckResponse result = deckService.addCardToDeck(1L, request);

            assertThat(result).isNotNull();
            assertThat(existingDeckCard.getQuantity()).isEqualTo(4);

            verify(deckServiceHelper).validateCardAddition(testDeck, testCard, 2);
            verify(deckServiceHelper).validateMaxCopiesLand(testCard, 4);
            verify(deckCardRepository).save(existingDeckCard);
        }

        @Test
        void addCardToDeck_WhenCardExists_ShouldUpdateQuantity() {
            AddCardDeckRequest request = new AddCardDeckRequest(1L, 2);
            DeckCardId deckCardId = new DeckCardId(1L, 1L);

            DeckCard existingDeckCard = new DeckCard();
            existingDeckCard.setId(deckCardId);
            existingDeckCard.setQuantity(2);
            existingDeckCard.setCard(testCard);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            when(userSecurityUtils.findCardById(request)).thenReturn(testCard);
            when(deckCardService.getExistingDeckCard(deckCardId)).thenReturn(existingDeckCard);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            DeckResponse result = deckService.addCardToDeck(1L, request);

            assertThat(result).isNotNull();
            assertThat(existingDeckCard.getQuantity()).isEqualTo(4);

            verify(deckServiceHelper).validateMaxCopiesLand(testCard, 4);
            verify(deckCardRepository).save(existingDeckCard);
        }

        @Test
        void addCardToDeck_whenUnauthorized_throw_exception() {
            testDeck.setIsPublic(false);
            AddCardDeckRequest request = new AddCardDeckRequest(1L, 2);

            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> deckService.addCardToDeck(1L,request ));
            assertEquals(new UnauthorizedModificationsException().getMessage(), exception.getMessage());
        }

        @Test
        void addCardToDeck_WhenCardNotExists() {
            AddCardDeckRequest request = new AddCardDeckRequest(1L, 2);
            DeckCardId deckCardId = new DeckCardId(1L, 1L);

            DeckCard newDeckCard = new DeckCard();
            newDeckCard.setId(deckCardId);
            newDeckCard.setQuantity(2);
            newDeckCard.setCard(testCard);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            when(userSecurityUtils.findCardById(request)).thenReturn(testCard);
            when(deckCardService.getExistingDeckCard(deckCardId)).thenReturn(null);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));


            DeckResponse result = deckService.addCardToDeck(1L, request);

            assertThat(result).isNotNull();

            verify(deckServiceHelper).validateCardAddition(testDeck, testCard, 2);
            verify(deckCardRepository).save(newDeckCard);
        }
    }

    @Nested
    class removeCardFromDeck{

        @Test
        void removeCardFromDeck_ShouldRemoveCardSuccessfully() {
            DeckCardId deckCardId = new DeckCardId(1L, 1L);
            DeckCard deckCard = new DeckCard();
            deckCard.setId(deckCardId);
            deckCard.setQuantity(3);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            DeckResponse result = deckService.removeCardFromDeck(1L, 1L, 2);

            assertThat(result).isNotNull();
            assertThat(deckCard.getQuantity()).isEqualTo(1);

            verify(deckCardRepository).save(deckCard);
            verify(deckRepository, times(2)).findById(1L);
        }

        @Test
        void removeCardFromDeck_WhenRemovingAllCopies_ShouldDeleteCard() {
            DeckCardId deckCardId = new DeckCardId(1L, 1L);
            DeckCard deckCard = new DeckCard();
            deckCard.setId(deckCardId);
            deckCard.setQuantity(2);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.of(deckCard));
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            DeckResponse result = deckService.removeCardFromDeck(1L, 1L, 3);

            assertThat(result).isNotNull();

            verify(deckCardRepository).delete(deckCard);
            verify(deckCardRepository, never()).save(any(DeckCard.class));
        }

        @Test
        void removeCardFromDeck_WhenCardNotInDeck_ShouldThrowException() {
            DeckCardId deckCardId = new DeckCardId(1L, 1L);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);
            when(deckCardRepository.findById(deckCardId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deckService.removeCardFromDeck(1L, 1L, 1))
                    .isInstanceOf(CardIdNotFoundInDeckException.class);

            verify(deckCardRepository).findById(deckCardId);
        }

        @Test
        void removeCardFromDeck_when_UserIsUnauthorized() {
            DeckCardId deckCardId = new DeckCardId(1L, 1L);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(false);

            assertThatThrownBy(() -> deckService.removeCardFromDeck(1L, 1L, 1))
                    .isInstanceOf(UnauthorizedModificationsException.class);
        }
    }
}
