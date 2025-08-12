package com.MagicTheGathering.card;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardResponse mockCardResponse;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        Set<String> cardTypes = new HashSet<>();
        cardTypes.add(CardType.ARTIFACT.name());

        Set<String> manaColors = new HashSet<>();
        manaColors.add(ManaColor.BLACK.name());

        Set<String> legalities = new HashSet<>();
        legalities.add(Legality.BRAWL.name());
                // Setup mock card response
        mockCardResponse = new CardResponse(
                1L,
                LocalDateTime.now(),
                "Lightning Bolt",
                cardTypes,
                "Test card",
                3,
                manaColors,
                "Lightning Bolt deals 3 damage to any target.",
                15,
                15,
                0,
                "Core Set 2021",
                137,
                "Christopher Rush",
                "M21",
                "http://test-image-url.com/image.jpg",
                legalities,
                4,
                1L
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void should_deleteCard_whenAuthorized() throws Exception {
        Long cardId = 1L;

        // Mock the service method to not throw any exception
        doNothing().when(cardService).deleteCard(cardId);

        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void should_getAllCardsByUser() throws Exception {
        // Create mock page response
        Page<CardResponse> mockPage = new PageImpl<>(Arrays.asList(mockCardResponse));
        when(cardService.getAllCardsByUser(eq(0), eq(4))).thenReturn(mockPage);

        mockMvc.perform(get("/api/cards/my-cards")
                        .param("page", "1")
                        .param("size", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Lightning Bolt"));

        verify(cardService, times(1)).getAllCardsByUser(0, 4);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void should_getCardById_whenCardExists() throws Exception {
        Long cardId = 1L;

        when(cardService.getCardById(cardId)).thenReturn(mockCardResponse);

        mockMvc.perform(get("/api/cards/{id}", cardId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.name").value("Lightning Bolt"));

        verify(cardService, times(1)).getCardById(cardId);
    }

//    @Test
//    @WithMockUser(roles = {"USER"})
//    void should_createCard_withValidRequest() throws Exception {
//        MockMultipartFile mockImg = new MockMultipartFile(
//                "image",
//                "test-product.jpg",
//                "image/jpeg",
//                "fake-image-content".getBytes());
//
//        when(cloudinaryService.uploadFile(any()))
//                .thenReturn(java.util.Map.of("secure_url", "http://example.com/test-image.jpg"));
//
//        when(cardService.createCard(any(CardRequest.class))).thenReturn(mockCardResponse);
//
//        mockMvc.perform(multipart("/api/cards")
//                        .file(mockImg)
//                        .param("name", "Lightning Bolt")
//                        .param("types", "ARTIFACT")
//                        .param("specificType", "Test card")
//                        .param("manaTotalCost", "3")
//                        .param("manaColors", "BLACK")
//                        .param("textRules", "Lightning Bolt deals 3 damage to any target.")
//                        .param("power", "15")
//                        .param("endurance", "15")
//                        .param("loyalty", "0")
//                        .param("collection", "Core Set 2021")
//                        .param("cartNumber", "137")
//                        .param("artist", "Christopher Rush")
//                        .param("edition", "M21")
//                        .param("legalityFormat", "BRAWL")
//                        .param("quantity", "4")
//                        .with(request -> {
//                            request.setMethod("POST");
//                            return request;
//                        })
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("Lightning Bolt"))
//                .andExpect(jsonPath("$.id").value(1L));
//
//        verify(cardService, times(1)).createCard(any(CardRequest.class));
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    void should_updateCard_whenAuthorized() throws Exception {
//        Long cardId = 1L;
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "image",
//                "test_image.png",
//                MediaType.IMAGE_PNG_VALUE,
//                "Test image.".getBytes(StandardCharsets.UTF_8)
//        );
//
//        when(cardService.updateCard(eq(cardId), any(CardRequest.class))).thenReturn(mockCardResponse);
//
//        mockMvc.perform(multipart("/api/cards/{id}", cardId)
//                        .file(mockFile)
//                        .param("name", "Lightning Bolt")
//                        .param("cardType", CardType.ARTIFACT.name())
//                        .param("specificType", "Test card")
//                        .param("manaTotalCost", "3")
//                        .param("manaColor", ManaColor.BLACK.name())
//                        .param("textRules", "Lightning Bolt deals 3 damage to any target.")
//                        .param("power", "15")
//                        .param("endurance", "15")
//                        .param("loyalty", "0")
//                        .param("collection", "Core Set 2021")
//                        .param("cartNumber", "137")
//                        .param("artist", "Christopher Rush")
//                        .param("edition", "M21")
//                        .param("legality", Legality.BRAWL.name())
//                        .param("quantity", "4")
//                        .with(request -> {
//                            request.setMethod("PUT");
//                            return request;
//                        })
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Lightning Bolt"))
//                .andExpect(jsonPath("$.id").value(cardId));
//
//        verify(cardService, times(1)).updateCard(eq(cardId), any(CardRequest.class));
//    }

    @Test
    @WithMockUser(roles = {"USER"})
    void should_returnDefaultPaginationValues_whenNoParamsProvided() throws Exception {
        Page<CardResponse> mockPage = new PageImpl<>(Arrays.asList(mockCardResponse));
        when(cardService.getAllCardsByUser(eq(0), eq(4))).thenReturn(mockPage);

        mockMvc.perform(get("/api/cards/my-cards")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(cardService, times(1)).getAllCardsByUser(0, 4);
    }

    @Test
    void should_returnUnauthorized_whenNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/cards/my-cards")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}