package com.MagicTheGathering.like;

import com.MagicTheGathering.Exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.like.dto.DeckLikeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeckLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckLikeService deckLikeService;

    @Autowired
    private ObjectMapper objectMapper;

    private DeckLikeResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = DeckLikeResponse.builder()
                .deckId(1L)
                .liked(true)
                .likeCount(5L)
                .build();
    }

    @Nested
    @WithMockUser(roles = "USER")
    class ManageLike {

        @Test
        void manageLike_When_ValidDeckId_Should_Return_DeckLikeResponse() throws Exception {
            when(deckLikeService.manageLike(1L)).thenReturn(testResponse);

            mockMvc.perform(post("/api/deck/1/like")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deckId").value(1))
                    .andExpect(jsonPath("$.liked").value(true))
                    .andExpect(jsonPath("$.likeCount").value(5));

            verify(deckLikeService).manageLike(1L);
        }

        @Test
        void manageLike_When_DeckNotFound_Should_Return_NotFound() throws Exception {
            when(deckLikeService.manageLike(1L)).thenThrow(new DeckIdNotFoundException(1L));

            mockMvc.perform(post("/api/deck/1/like")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(deckLikeService).manageLike(1L);
        }

        @Test
        void manageLike_When_PrivateDeck_Should_Return_NotFound() throws Exception {
            when(deckLikeService.manageLike(1L)).thenThrow(new AccessDeniedPrivateDeckException());

            mockMvc.perform(post("/api/deck/1/like")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(deckLikeService).manageLike(1L);
        }
    }

    @Nested
    @WithMockUser(roles = "USER")
    class GetLikesByDeck {

        @Test
        void getLikesByDeck_When_ValidDeckId_Should_Return_DeckLikeResponse() throws Exception {
            when(deckLikeService.getLikesByDeckId(1L)).thenReturn(testResponse);
            System.out.println(testResponse.likeCount());
            System.out.println(testResponse.liked());
            System.out.println(testResponse.deckId());

            mockMvc.perform(get("/api/deck/1/like")
                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deckId").value(1))
                    .andExpect(jsonPath("$.liked").value(true))
                    .andExpect(jsonPath("$.likeCount").value(5));

            verify(deckLikeService).getLikesByDeckId(1L);
        }

        @Test
        void getLikesByDeck_When_DeckNotFound_Should_Return_NotFound() throws Exception {
            when(deckLikeService.getLikesByDeckId(1L)).thenThrow(new DeckIdNotFoundException(1L));

            mockMvc.perform(get("/api/deck/1/like")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(deckLikeService).getLikesByDeckId(1L);
        }

        @Test
        void getLikesByDeck_When_UserNotLiked_Should_Return_False() throws Exception {
            DeckLikeResponse notLikedResponse = DeckLikeResponse.builder()
                    .deckId(1L)
                    .liked(false)
                    .likeCount(3L)
                    .build();

            when(deckLikeService.getLikesByDeckId(1L)).thenReturn(notLikedResponse);

            mockMvc.perform(get("/api/deck/1/like")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deckId").value(1))
                    .andExpect(jsonPath("$.liked").value(false))
                    .andExpect(jsonPath("$.likeCount").value(3));
        }
    }
}
