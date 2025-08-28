package com.MagicTheGathering.like;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckLikeTest {
    @Test
    void onCreate() {
        DeckLike deckLike = new DeckLike();

        deckLike.onCreate();

        assertNotNull(deckLike.getCreatedAt());
    }
}
