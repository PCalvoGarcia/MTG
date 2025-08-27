package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.exceptions.InvalidFormatsException;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserRepository;
import com.MagicTheGathering.user.exceptions.UserIdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DeckSearchServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeckSearchService deckSearchService;

    private User user;
    private Deck deck1;
    private Deck deck2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        deck1 = Deck.builder()
                .id(1L)
                .deckName("Standard Deck")
                .isPublic(true)
                .type(Legality.STANDARD)
                .maxCards(60)
                .user(user)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        deck2 = Deck.builder()
                .id(2L)
                .deckName("Modern Deck")
                .isPublic(true)
                .type(Legality.MODERN)
                .maxCards(60)
                .user(user)
                .deckCards(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getDecksByFormat_shouldReturnDecksForValidFormat() { 
        List<Deck> expectedDecks = Arrays.asList(deck1);
        when(deckRepository.findByTypeAndIsPublicTrue(Legality.STANDARD))
                .thenReturn(expectedDecks); 

        List<DeckResponse> result = deckSearchService.getDecksByFormat("STANDARD");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Standard Deck", result.get(0).deckName());
        assertEquals("STANDARD", result.get(0).type());
        verify(deckRepository).findByTypeAndIsPublicTrue(Legality.STANDARD);
    }

    @Test
    void getDecksByFormat_shouldReturnDecksForValidFormatLowerCase() { 
        List<Deck> expectedDecks = Arrays.asList(deck1);
        when(deckRepository.findByTypeAndIsPublicTrue(Legality.STANDARD))
                .thenReturn(expectedDecks); 

        List<DeckResponse> result = deckSearchService.getDecksByFormat("standard");
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deckRepository).findByTypeAndIsPublicTrue(Legality.STANDARD);
    }

    @Test
    void getDecksByFormat_shouldReturnEmptyListWhenNoDecks() { 
        when(deckRepository.findByTypeAndIsPublicTrue(Legality.COMMANDER))
                .thenReturn(Arrays.asList()); 

        List<DeckResponse> result = deckSearchService.getDecksByFormat("COMMANDER");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deckRepository).findByTypeAndIsPublicTrue(Legality.COMMANDER);
    }

    @Test
    void getDecksByFormat_shouldThrowExceptionForInvalidFormat() {
        InvalidFormatsException exception = assertThrows(
                InvalidFormatsException.class,
                () -> deckSearchService.getDecksByFormat("INVALID_FORMAT")
        );

        assertNotNull(exception);
        verify(deckRepository, never()).findByTypeAndIsPublicTrue(any());
    }

    @Test
    void getPublicDecksByUser_shouldReturnDecksForValidUser() { 
        Long userId = 1L;
        List<Deck> expectedDecks = Arrays.asList(deck1, deck2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deckRepository.findByUserAndIsPublicTrue(user)).thenReturn(expectedDecks); 

        List<DeckResponse> result = deckSearchService.getPublicDecksByUser(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Standard Deck", result.get(0).deckName());
        assertEquals("Modern Deck", result.get(1).deckName());
        verify(userRepository).findById(userId);
        verify(deckRepository).findByUserAndIsPublicTrue(user);
    }

    @Test
    void getPublicDecksByUser_shouldReturnEmptyListWhenUserHasNoPublicDecks() { 
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deckRepository.findByUserAndIsPublicTrue(user)).thenReturn(Arrays.asList()); 

        List<DeckResponse> result = deckSearchService.getPublicDecksByUser(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
        verify(deckRepository).findByUserAndIsPublicTrue(user);
    }

    @Test
    void getPublicDecksByUser_shouldThrowExceptionForInvalidUserId() { 
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty()); 

        UserIdNotFoundException exception = assertThrows(
                UserIdNotFoundException.class,
                () -> deckSearchService.getPublicDecksByUser(userId)
        );

        assertNotNull(exception);
        verify(userRepository).findById(userId);
        verify(deckRepository, never()).findByUserAndIsPublicTrue(any());
    }
}