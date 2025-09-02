package com.MagicTheGathering.like;

import com.MagicTheGathering.Exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.like.dto.DeckLikeResponse;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckLikeServiceTest {

    @Mock
    private DeckLikeRepository deckLikeRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DeckLikeService deckLikeService;

    private User testUser;
    private Deck testDeck;
    private DeckLike testDeckLike;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .build();

        testDeck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .user(testUser)
                .build();

        testDeckLike = DeckLike.builder()
                .id(1L)
                .user(testUser)
                .deck(testDeck)
                .build();
    }

    @Nested
    class ManageLike {

        @Test
        void manageLike_When_DeckNotFound_Should_Throw_DeckIdNotFoundException() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(DeckIdNotFoundException.class, () -> {
                deckLikeService.manageLike(1L);
            });

            verify(deckRepository).findById(1L);
        }

        @Test
        void manageLike_When_DeckIsPrivate_Should_Throw_AccessDeniedPrivateDeckException() {
            testDeck.setIsPublic(false);

            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));

            assertThrows(AccessDeniedPrivateDeckException.class, () -> {
                deckLikeService.manageLike(1L);
            });

            verify(userService).getAuthenticatedUser();
            verify(deckRepository).findById(1L);
        }

        @Test
        void manageLike_When_UserNotLikedDeck_Should_AddLike() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckLikeRepository.findByUserAndDeck(testUser, testDeck)).thenReturn(Optional.empty());
            when(deckLikeRepository.existsByUserAndDeck(testUser, testDeck)).thenReturn(true);
            when(deckLikeRepository.countByDeck(testDeck)).thenReturn(1L);

            DeckLikeResponse result = deckLikeService.manageLike(1L);

            assertThat(result).isNotNull();
            assertThat(result.deckId()).isEqualTo(1L);
            assertThat(result.liked()).isTrue();
            assertThat(result.likeCount()).isEqualTo(1L);

            verify(deckLikeRepository).save(any(DeckLike.class));
            verify(deckLikeRepository, never()).delete(any());
        }

        @Test
        void manageLike_When_UserAlreadyLikedDeck_Should_RemoveLike() {
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(deckLikeRepository.findByUserAndDeck(testUser, testDeck)).thenReturn(Optional.of(testDeckLike));
            when(deckLikeRepository.existsByUserAndDeck(testUser, testDeck)).thenReturn(false);
            when(deckLikeRepository.countByDeck(testDeck)).thenReturn(0L);

            DeckLikeResponse result = deckLikeService.manageLike(1L);

            assertThat(result).isNotNull();
            assertThat(result.deckId()).isEqualTo(1L);
            assertThat(result.liked()).isFalse();
            assertThat(result.likeCount()).isEqualTo(0L);

            verify(deckLikeRepository).delete(testDeckLike);
            verify(deckLikeRepository, never()).save(any());
        }
    }

    @Nested
    class GetLikesByDeckId {

        @Test
        void getLikesByDeckId_When_DeckNotFound_Should_Throw_DeckIdNotFoundException() {
            when(deckRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(DeckIdNotFoundException.class, () -> {
                deckLikeService.getLikesByDeckId(1L);
            });

            verify(deckRepository).findById(1L);
        }

        @Test
        void getLikesByDeckId_When_DeckExists_Should_Return_LikeInfo() {
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeRepository.existsByUserAndDeck(testUser, testDeck)).thenReturn(true);
            when(deckLikeRepository.countByDeck(testDeck)).thenReturn(5L);

            DeckLikeResponse result = deckLikeService.getLikesByDeckId(1L);

            assertThat(result).isNotNull();
            assertThat(result.deckId()).isEqualTo(1L);
            assertThat(result.liked()).isTrue();
            assertThat(result.likeCount()).isEqualTo(5L);

            verify(deckRepository).findById(1L);
            verify(userService).getAuthenticatedUser();
            verify(deckLikeRepository).existsByUserAndDeck(testUser, testDeck);
            verify(deckLikeRepository).countByDeck(testDeck);
        }

        @Test
        void getLikesByDeckId_When_UserNotLikedDeck_Should_Return_False() {
            when(deckRepository.findById(1L)).thenReturn(Optional.of(testDeck));
            when(userService.getAuthenticatedUser()).thenReturn(testUser);
            when(deckLikeRepository.existsByUserAndDeck(testUser, testDeck)).thenReturn(false);
            when(deckLikeRepository.countByDeck(testDeck)).thenReturn(3L);

            DeckLikeResponse result = deckLikeService.getLikesByDeckId(1L);

            assertThat(result.liked()).isFalse();
            assertThat(result.likeCount()).isEqualTo(3L);
        }
    }

    @Nested
    class GetDeckLikedByUser {

        @Test
        void getDeckLikedByUser_When_UserHasLikedDecks_Should_Return_List() {
            List<DeckLike> expectedLikes = List.of(testDeckLike);
            when(deckLikeRepository.findByUser(testUser)).thenReturn(expectedLikes);

            List<DeckLike> result = deckLikeService.getDeckLikedByUser(testUser);

            assertThat(result).isEqualTo(expectedLikes);
            verify(deckLikeRepository).findByUser(testUser);
        }

        @Test
        void getDeckLikedByUser_When_UserHasNoLikedDecks_Should_Return_EmptyList() {
            when(deckLikeRepository.findByUser(testUser)).thenReturn(List.of());

            List<DeckLike> result = deckLikeService.getDeckLikedByUser(testUser);

            assertThat(result).isEmpty();
            verify(deckLikeRepository).findByUser(testUser);
        }
    }

    @Nested
    class DeleteLikesByDeckId {

        @Test
        void deleteLikesByDeckId_Should_CallRepository() {
            deckLikeService.deleteLikesByDeckId(1L);

            verify(deckLikeRepository).deleteByDeckId(1L);
        }
    }
}