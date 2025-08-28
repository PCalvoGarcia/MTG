package com.MagicTheGathering.like;

import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.like.dto.DeckLikeMapperDto;
import com.MagicTheGathering.like.dto.DeckLikeResponse;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckLikeMapperDtoTest {

    private Deck testDeck;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        testDeck = Deck.builder()
                .id(1L)
                .deckName("Test Deck")
                .isPublic(true)
                .user(testUser)
                .build();
    }

    @Test
    void fromEntity_WhenLikedTrue_ShouldReturnCorrectResponse() {
        DeckLikeResponse result = DeckLikeMapperDto.fromEntity(testDeck, true, 10L);

        assertThat(result).isNotNull();
        assertThat(result.deckId()).isEqualTo(1L);
        assertThat(result.liked()).isTrue();
        assertThat(result.likeCount()).isEqualTo(10L);
    }

    @Test
    void fromEntity_WhenLikedFalse_ShouldReturnCorrectResponse() {
        DeckLikeResponse result = DeckLikeMapperDto.fromEntity(testDeck, false, 5L);

        assertThat(result).isNotNull();
        assertThat(result.deckId()).isEqualTo(1L);
        assertThat(result.liked()).isFalse();
        assertThat(result.likeCount()).isEqualTo(5L);
    }

    @Test
    void fromEntity_WhenZeroLikes_ShouldReturnCorrectResponse() {
        DeckLikeResponse result = DeckLikeMapperDto.fromEntity(testDeck, false, 0L);

        assertThat(result).isNotNull();
        assertThat(result.deckId()).isEqualTo(1L);
        assertThat(result.liked()).isFalse();
        assertThat(result.likeCount()).isEqualTo(0L);
    }

    @Test
    void fromEntity_WhenNullDeck_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            DeckLikeMapperDto.fromEntity(null, true, 5L);
        });
    }

    @Test
    void fromEntity_WhenHighLikeCount_ShouldHandleCorrectly() {
        long highCount = 999999L;

        DeckLikeResponse result = DeckLikeMapperDto.fromEntity(testDeck, true, highCount);

        assertThat(result.likeCount()).isEqualTo(highCount);
    }
}
