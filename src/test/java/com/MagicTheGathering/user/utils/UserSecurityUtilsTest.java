package com.MagicTheGathering.user.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserSecurityUtilsTest {

    @Mock
    private UserService userService;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserSecurityUtils userSecurityUtils;

    private User authenticatedUser;
    private User otherUser;
    private Card card;
    private Deck deck;

    @BeforeEach
    void setup() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("authenticatedUser");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");

        card = new Card();
        card.setId(1L);
        card.setUser(authenticatedUser);

        deck = new Deck();
        deck.setId(1L);
        deck.setUser(authenticatedUser);
    }


    @Test
    void when_createUserByUserDetails_return_createsValidUserDetails() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails result = UserSecurityUtils.createUserByUserDetails(user, authorities);

        assertEquals("testUser", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.isEnabled());
        assertEquals(new HashSet<>(authorities), new HashSet<>(result.getAuthorities()));
    }

    @Test
    void getAuthoritiesRole() {
        User user = new User();
        Set<Role> roles = Set.of(Role.USER, Role.ADMIN);
        user.setRoles(roles);

        List<GrantedAuthority> result = UserSecurityUtils.getAuthoritiesRole(user);

        List<String> authorities = result.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertEquals(2, authorities.size());
    }

    @Test
    void getAuthoritiesRole_shouldReturnEmptyList_whenUserHasNoRoles() {
        User user = new User();
        user.setRoles(Set.of());

        List<GrantedAuthority> result = UserSecurityUtils.getAuthoritiesRole(user);

        assertTrue(result.isEmpty());
    }

    @Nested
    class IsAuthorizedToModifyCard {

        @Test
        void shouldReturnTrue_whenUserOwnsCard() {
            when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);

            boolean result = userSecurityUtils.isAuthorizedToModifyCard(card);

            assertTrue(result);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldReturnFalse_whenUserDoesNotOwnCard() {
            card.setUser(otherUser);
            when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);

            boolean result = userSecurityUtils.isAuthorizedToModifyCard(card);

            assertFalse(result);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldReturnFalse_whenAuthenticatedUserIdIsNull() {
            User userWithNullId = new User();
            userWithNullId.setId(null);
            when(userService.getAuthenticatedUser()).thenReturn(userWithNullId);

            boolean result = userSecurityUtils.isAuthorizedToModifyCard(card);

            assertFalse(result);
            verify(userService).getAuthenticatedUser();
        }
    }

    @Nested
    class IsAuthorizedToModifyDeck {

        @Test
        void shouldReturnTrue_whenUserOwnsDeck() {
            when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);

            boolean result = userSecurityUtils.isAuthorizedToModifyDeck(deck);

            assertTrue(result);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldReturnFalse_whenUserDoesNotOwnDeck() {
            deck.setUser(otherUser);
            when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);

            boolean result = userSecurityUtils.isAuthorizedToModifyDeck(deck);

            assertFalse(result);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldReturnFalse_whenAuthenticatedUserIdIsNull() {
            User userWithNullId = new User();
            userWithNullId.setId(null);
            when(userService.getAuthenticatedUser()).thenReturn(userWithNullId);

            boolean result = userSecurityUtils.isAuthorizedToModifyDeck(deck);

            assertFalse(result);
            verify(userService).getAuthenticatedUser();
        }
    }

    @Nested
    class FindCardById {

        @Test
        void shouldReturnCard_whenCardExists() {
            AddCardDeckRequest request = new AddCardDeckRequest(1L, 2);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

            Card result = userSecurityUtils.findCardById(request);

            assertNotNull(result);
            assertEquals(card, result);
            assertEquals(1L, result.getId());
            verify(cardRepository).findById(1L);
        }

        @Test
        void shouldThrowRuntimeException_whenCardDoesNotExist() {
            AddCardDeckRequest request = new AddCardDeckRequest(999L, 2);
            when(cardRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userSecurityUtils.findCardById(request);
            });

            assertEquals("card not found", exception.getMessage());
            verify(cardRepository).findById(999L);
        }

        @Test
        void shouldCallRepositoryWithCorrectId_whenMultipleCalls() {
            AddCardDeckRequest request1 = new AddCardDeckRequest(1L, 2);
            AddCardDeckRequest request2 = new AddCardDeckRequest(2L, 3);

            Card card2 = new Card();
            card2.setId(2L);

            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));

            Card result1 = userSecurityUtils.findCardById(request1);
            Card result2 = userSecurityUtils.findCardById(request2);

            assertEquals(1L, result1.getId());
            assertEquals(2L, result2.getId());
            verify(cardRepository).findById(1L);
            verify(cardRepository).findById(2L);
        }
    }
}