package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
class DeckCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckCardService deckCardService;

    @Autowired
    private ObjectMapper objectMapper;

    private DeckCardResponse testDeckCard;
    private CardResponse card;

    @BeforeEach
    void setUp() {
        card = new CardResponse(
                1L,
                LocalDateTime.now(),
                "Lightning Bolt",
                Set.of("Instant"),
                "Instant",
                1,
                Set.of("Red"),
                "Deal 3 damage to any target.",
                0,
                0,
                0,
                "M10",
                150,
                "Christopher Moeller",
                "Core Set 2010",
                "http://example.com/lightning-bolt.jpg",
                Set.of("Standard", "Modern"),
                10,
                1L
        );

        testDeckCard = new DeckCardResponse(card, 3);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCardsByDeckId_ShouldReturnCards() throws Exception {
        when(deckCardService.getCardsByDeckId(1L)).thenReturn(List.of(testDeckCard));

        mockMvc.perform(get("/api/deck-cards/deck/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].card.id").value(1L))
                .andExpect(jsonPath("$[0].card.name").value("Lightning Bolt"))
                .andExpect(jsonPath("$[0].quantity").value(3));

        verify(deckCardService).getCardsByDeckId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDeckCard_ShouldReturnCard() throws Exception {
        when(deckCardService.getDeckCard(1L, 1L)).thenReturn(testDeckCard);

        mockMvc.perform(get("/api/deck-cards/deck/1/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.id").value(1L))
                .andExpect(jsonPath("$.card.name").value("Lightning Bolt"));

        verify(deckCardService).getDeckCard(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCardQuantity_ShouldUpdateSuccessfully() throws Exception {
        DeckCardResponse testDeckCard2 = new DeckCardResponse(card, 5);
        when(deckCardService.updateDeckCardQuantity(1L, 1L, 5)).thenReturn(
                testDeckCard2
        );

        mockMvc.perform(put("/api/deck-cards/deck/1/card/1/quantity")
                        .with(csrf())
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));

        verify(deckCardService).updateDeckCardQuantity(1L, 1L, 5);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCardQuantity_WhenNull_ShouldReturnNoContent() throws Exception {
        when(deckCardService.updateDeckCardQuantity(1L, 1L, 0)).thenReturn(null);

        mockMvc.perform(put("/api/deck-cards/deck/1/card/1/quantity")
                        .with(csrf())
                        .param("quantity", "0"))
                .andExpect(status().isNoContent());

        verify(deckCardService).updateDeckCardQuantity(1L, 1L, 0);
    }

    @Test
    @WithMockUser(roles = "USER")
    void removeDeckCard_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(deckCardService).removeDeckCard(1L, 1L);

        mockMvc.perform(delete("/api/deck-cards/deck/1/card/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(deckCardService).removeDeckCard(1L, 1L);
    }
}
