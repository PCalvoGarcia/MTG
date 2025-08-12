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
import com.MagicTheGathering.deckCard.exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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

    @InjectMocks
    private DeckService deckService;

    private User testUser;
    private Deck testDeck;
    private Card testCard;
    private DeckRequest deckRequest;

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

        deckRequest = new DeckRequest(
                "New Deck",
                true,
                Legality.STANDARD,
                1L
        );
    }

    @Test
    void getAllDeckByUser_ShouldReturnUserDecks() {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Deck> deckPage = new PageImpl<>(List.of(testDeck));

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findByUser(testUser, pageable)).thenReturn(deckPage);

        Page<DeckResponse> result = deckService.getAllDeckByUser(0, 4);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).deckName()).isEqualTo("Test Deck");

        verify(userService).getAuthenticatedUser();
        verify(deckRepository).findByUser(testUser, pageable);
    }

    @Test
    void getAllPublicDecks_ShouldReturnPublicDecks() {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Deck> deckPage = new PageImpl<>(List.of(testDeck));

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findByIsPublicTrue(pageable)).thenReturn(deckPage);

        Page<DeckResponse> result = deckService.getAllPublicDecks(0, 4);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isPublic()).isTrue();

        verify(deckRepository).findByIsPublicTrue(pageable);
    }

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
                .hasMessageContaining("deck not found");

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
                .hasMessageContaining("unauthorized");

        verify(deckRepository).findById(1L);
        verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
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

    @Test
    void updateDeck_WhenAuthorized_ShouldReturnUpdatedDeck() {
        DeckRequest updateRequest = new DeckRequest(
                "Updated Deck",
                false,
                Legality.MODERN,
                1L
        );

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
        when(userSecurityUtils.isAuthorizedToModifyDeck(testDeck)).thenReturn(true);

        DeckResponse result = deckService.updateDeck(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(testDeck.getDeckName()).isEqualTo("Updated Deck");
        assertThat(testDeck.getIsPublic()).isFalse();
        assertThat(testDeck.getType()).isEqualTo(Legality.MODERN);

        verify(deckRepository).findById(1L);
        verify(userSecurityUtils).isAuthorizedToModifyDeck(testDeck);
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
    void deleteDeck_WhenAuthorized_ShouldDeleteDeck() {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

        deckService.deleteDeck(1L);

        verify(deckRepository).findById(1L);
        verify(deckRepository).delete(testDeck);
    }

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
    void updateDeck_WhenDeckNotFound_ShouldThrowException() {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deckService.updateDeck(999L, deckRequest))
                .isInstanceOf(DeckIdNotFoundException.class);

        verify(deckRepository).findById(999L);
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
