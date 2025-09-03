package com.MagicTheGathering.card;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.Exceptions.EmptyListException;
import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.card.exceptions.CardIdNotFoundException;
import com.MagicTheGathering.card.exceptions.DeleteCardNotAllowedException;
import com.MagicTheGathering.card.utils.CardServiceHelper;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deckCard.DeckCardRepository;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserSecurityUtils userSecurityUtils;

    @Mock
    private CardServiceHelper cardServiceHelper;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private DeckCardRepository deckCardRepository;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;
    private CardRequest cardRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        testCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .types(Set.of(CardType.INSTANT))
                .specificType("Instant")
                .manaTotalCost(1)
                .manaColors(Set.of(ManaColor.RED))
                .textRules("Lightning Bolt deals 3 damage to any target.")
                .power(0)
                .endurance(0)
                .loyalty(0)
                .collection("Core Set 2021")
                .cardNumber(137)
                .artist("Christopher Rush")
                .edition("M21")
                .imageUrl("https://example.com/lightning_bolt.jpg")
                .legalityFormat(Set.of(Legality.STANDARD))
                .quantity(4)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .deckCards(new HashSet<>())
                .build();

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        cardRequest = new CardRequest(
                "Lightning Bolt",
                CardType.INSTANT,
                "Instant",
                1,
                 Set.of(ManaColor.RED),
                "Lightning Bolt deals 3 damage to any target.",
                0,
                0,
                0,
                "Core Set 2021",
                137,
                "Christopher Rush",
                "M21",
                mockFile,
                Set.of(Legality.STANDARD),
                4
        );
    }

    @Nested
    class GetAllCardsByUser {
        @Test
        void getAllCardsByUser_should_return_card_whenListCardsExists() {
            List<CardResponse> cardResponseList = new ArrayList<>();
            cardResponseList.add(CardMapperDto.fromEntity(testCard));
            cardResponseList.add(CardMapperDto.fromEntity(testCard));

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardServiceHelper.getCardResponseList(testUser)).thenReturn(cardResponseList);

            List<CardResponse> result = cardService.getAllCardsByUser();

            assertNotNull(result);
            assertEquals("Lightning Bolt", result.getFirst().name());
            assertEquals(1L, result.getFirst().id());
        }

        @Test
        void getAllCardsByUser_should_throwEmptyListException_whenListCardIsEmpty() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);

            RuntimeException exception = assertThrows(EmptyListException.class,
                    () -> cardService.getAllCardsByUser());

        }
    }

    @Nested
    class GetCardById {
        @Test
        void getCardById_should_return_card_whenCardExists() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);

            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

            CardResponse result = cardService.getCardById(1L);

            assertNotNull(result);
            assertEquals("Lightning Bolt", result.name());
            assertEquals(1L, result.id());

            verify(cardRepository).findById(1L);
        }

        @Test
        void getCardById_should_throwException_when_cardNotExists() {
            when(cardRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CardIdNotFoundException.class, () -> cardService.getCardById(1L));

            verify(cardRepository).findById(1L);
        }
    }

    @Test
    void createCard_should_returnCardResponse_when_successful() throws Exception {
        Map<String, Object> uploadResult = Map.of("secure_url", "https://cloudinary.com/image.jpg");

        when(cardServiceHelper.getCardWithCloudinary(cardRequest, testUser)).thenReturn(CardMapperDto.fromEntity(testCard));
        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        CardResponse result = cardService.createCard(cardRequest);

        assertNotNull(result);
        assertEquals("Lightning Bolt", result.name());

        verify(userService).getAuthenticatedUser();
    }

    @Nested
    class UpdateCard {
        @Test
        void updateCard_should_returnUpdatedCard_when_authorized() {
            Card updatedCard = Card.builder()
                    .id(1L)
                    .name("Updated Lightning Bolt")
                    .imageUrl("https://example.com/updated.jpg")
                    .user(testUser)
                    .createdAt(testCard.getCreatedAt())
                    .build();

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
            when(userSecurityUtils.isAuthorizedToModifyCard(testCard)).thenReturn(true);

            CardResponse result = cardService.updateCard(1L, cardRequest);

            assertNotNull(result);

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyCard(testCard);
        }

        @Test
        void updateCard_shouldThrowException_when_notAuthorized() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
            when(userSecurityUtils.isAuthorizedToModifyCard(testCard)).thenReturn(false);

            assertThrows(UnauthorizedModificationsException.class,
                    () -> cardService.updateCard(1L, cardRequest));

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyCard(testCard);
        }

        @Test
        void updateCard_should_throwException_when_CardNotFound() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CardIdNotFoundException.class,
                    () -> cardService.updateCard(1L, cardRequest));

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
        }

    }

    @Nested
    class DeleteCard {
        @Test
        void deleteCard_shouldDeleteCard_when_AuthorizedAndNotInDecks() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
            when(userSecurityUtils.isAuthorizedToModifyCard(testCard)).thenReturn(true);
            when(deckCardRepository.existsByCard(testCard)).thenReturn(false);
            try (MockedStatic<CardServiceHelper> mocked = Mockito.mockStatic(CardServiceHelper.class)) {
                mocked.when(() -> CardServiceHelper.getPublicIdCloudinary(testCard.getImageUrl()))
                        .thenReturn("public_id");

                assertDoesNotThrow(() -> cardService.deleteCard(1L));

                verify(userService).getAuthenticatedUser();
                verify(cardRepository).findById(1L);
                verify(userSecurityUtils).isAuthorizedToModifyCard(testCard);
                verify(deckCardRepository).existsByCard(testCard);
                verify(cardServiceHelper).deleteImageCloudinary("public_id");
                verify(cardRepository).delete(testCard);
            }
        }

        @Test
        void deleteCard_should_throwException_whenCardIsInDeck() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
            when(userSecurityUtils.isAuthorizedToModifyCard(testCard)).thenReturn(true);
            when(deckCardRepository.existsByCard(testCard)).thenReturn(true);

            assertThrows(DeleteCardNotAllowedException.class,
                    () -> cardService.deleteCard(1L));

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyCard(testCard);
            verify(deckCardRepository).existsByCard(testCard);
            verify(cardRepository, never()).delete(any());
        }

        @Test
        void deleteCard_should_tThrowException_whenNotAuthorized() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
            when(userSecurityUtils.isAuthorizedToModifyCard(testCard)).thenReturn(false);

            assertThrows(UnauthorizedModificationsException.class,
                    () -> cardService.deleteCard(1L));

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
            verify(userSecurityUtils).isAuthorizedToModifyCard(testCard);
            verify(deckCardRepository, never()).existsByCard(any());
            verify(cardRepository, never()).delete(any());
        }

        @Test
        void deleteCard_should_throwException_when_CardNotFound() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(cardRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CardIdNotFoundException.class,
                    () -> cardService.deleteCard(1L));

            verify(userService).getAuthenticatedUser();
            verify(cardRepository).findById(1L);
            verify(cardRepository, never()).delete(any());
        }
    }


}
