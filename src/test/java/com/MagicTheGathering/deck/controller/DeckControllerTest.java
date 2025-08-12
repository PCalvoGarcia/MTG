package com.MagicTheGathering.deck.controller;

import com.MagicTheGathering.deck.DeckService;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.legality.Legality;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeckService deckService;

    private DeckResponse testDeckResponse;
    private DeckRequest testDeckRequest;
    private AddCardDeckRequest addCardRequest;

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

        testDeckRequest = new DeckRequest(
                "Test Deck",
                true,
                Legality.STANDARD,
                1L
        );

        addCardRequest = new AddCardDeckRequest(1L, 2);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyDecks_WithValidUser_ShouldReturnDecks() throws Exception {
        // Given
        Page<DeckResponse> deckPage = new PageImpl<>(List.of(testDeckResponse));
        when(deckService.getAllDeckByUser(0, 4)).thenReturn(deckPage);

        // When & Then
        mockMvc.perform(get("/api/decks/my-decks")
                        .param("page", "1")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].deckName").value("Test Deck"))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(deckService).getAllDeckByUser(0, 4);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPublicDecks_WithoutAuthentication_ShouldReturnDecks() throws Exception {
        // Given
        Page<DeckResponse> deckPage = new PageImpl<>(List.of(testDeckResponse));
        when(deckService.getAllPublicDecks(0, 4)).thenReturn(deckPage);

        // When & Then
        mockMvc.perform(get("/api/decks/public")
                        .param("page", "1")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].deckName").value("Test Deck"));

        verify(deckService).getAllPublicDecks(0, 4);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDeckById_WithValidId_ShouldReturnDeck() throws Exception {
        // Given
        when(deckService.getDeckById(1L)).thenReturn(testDeckResponse);

        // When & Then
        mockMvc.perform(get("/api/decks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deckName").value("Test Deck"))
                .andExpect(jsonPath("$.isPublic").value(true));

        verify(deckService).getDeckById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createNewDeck_WithValidRequest_ShouldCreateDeck() throws Exception {
        // Given
        when(deckService.createDeck(any(DeckRequest.class))).thenReturn(testDeckResponse);

        // When & Then
        mockMvc.perform(post("/api/decks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDeckRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deckName").value("Test Deck"))
                .andExpect(jsonPath("$.id").value(1));

        verify(deckService).createDeck(any(DeckRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createNewDeck_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        DeckRequest invalidRequest = new DeckRequest(
                "", // Invalid: empty name
                true,
                Legality.STANDARD,
                1L
        );

        // When & Then
        mockMvc.perform(post("/api/decks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(deckService, never()).createDeck(any(DeckRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateDeckById_WithValidRequest_ShouldUpdateDeck() throws Exception {
        // Given
        DeckResponse updatedDeck = new DeckResponse(
                1L, "Updated Deck", false, "MODERN", 60, 20, 1L, Collections.emptyList()
        );
        when(deckService.updateDeck(eq(1L), any(DeckRequest.class))).thenReturn(updatedDeck);

        // When & Then
        mockMvc.perform(put("/api/decks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDeckRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deckName").value("Updated Deck"))
                .andExpect(jsonPath("$.isPublic").value(false));

        verify(deckService).updateDeck(eq(1L), any(DeckRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteDeckById_WithValidId_ShouldDeleteDeck() throws Exception {
        // Given
        doNothing().when(deckService).deleteDeck(1L);

        // When & Then
        mockMvc.perform(delete("/api/decks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(deckService).deleteDeck(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void addCardToDeck_WithValidRequest_ShouldAddCard() throws Exception {
        // Given
        when(deckService.addCardToDeck(eq(1L), any(AddCardDeckRequest.class)))
                .thenReturn(testDeckResponse);

        // When & Then
        mockMvc.perform(post("/api/decks/1/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deckName").value("Test Deck"));

        verify(deckService).addCardToDeck(eq(1L), any(AddCardDeckRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addCardToDeck_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        AddCardDeckRequest invalidRequest = new AddCardDeckRequest(null, 0); // Invalid: null cardId, zero quantity

        // When & Then
        mockMvc.perform(post("/api/decks/1/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(deckService, never()).addCardToDeck(anyLong(), any(AddCardDeckRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void removeCardFromDeck_WithValidRequest_ShouldRemoveCard() throws Exception {
        // Given
        when(deckService.removeCardFromDeck(1L, 1L, 2)).thenReturn(testDeckResponse);

        // When & Then
        mockMvc.perform(delete("/api/decks/1/cards/1")
                        .with(csrf())
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deckName").value("Test Deck"));

        verify(deckService).removeCardFromDeck(1L, 1L, 2);
    }
}