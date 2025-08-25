package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeckSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckSearchService deckSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    private DeckResponse testDeckResponse;

    @BeforeEach
    void setUp() {
        testDeckResponse = new DeckResponse(
                1L,
                "Test Deck",
                true,
                "STANDARD",
                60,
                20,
                1L,
                Collections.emptyList()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDecksByFormat_ShouldReturnDecks() throws Exception {
        List<DeckResponse> deckPage = List.of(testDeckResponse);
        when(deckSearchService.getDecksByFormat(eq("STANDARD"))).thenReturn(deckPage);

        mockMvc.perform(get("/api/decks/search/by-format/STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].deckName").value("Test Deck"))
                .andExpect(jsonPath("$.[0].type").value("STANDARD"));

        verify(deckSearchService).getDecksByFormat(eq("STANDARD"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPublicDecksByUser_ShouldReturnDecks() throws Exception {
        List<DeckResponse> deckPage = List.of(testDeckResponse);
        when(deckSearchService.getPublicDecksByUser(eq(1L))).thenReturn(deckPage);

        mockMvc.perform(get("/api/decks/search/by-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].deckName").value("Test Deck"))
                .andExpect(jsonPath("$.[0].userId").value(1));

        verify(deckSearchService).getPublicDecksByUser(eq(1L));
    }
}
