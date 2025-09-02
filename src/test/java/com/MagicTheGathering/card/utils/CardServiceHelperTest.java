package com.MagicTheGathering.card.utils;


import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceHelperTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CardServiceHelper cardServiceHelper;

    private User testUser;
    private Card testCard;
    private CardRequest cardRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        testCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .imageUrl("https://cloudinary.com/test/image.jpg")
                .build();

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        cardRequest = new CardRequest(
                "Lightning Bolt",
                CardType.INSTANT,
                "Instant",
                1,
                Set.of(ManaColor.RED),
                "Lightning Bolt deals 3 damage to any target.",
                0,
                0,
                0,
                "Core Set 2021",
                137,
                "Christopher Rush",
                "M21",
                mockFile,
                Set.of(Legality.STANDARD),
                4
        );
    }

    @Test
    void when_getSavedCard_should_ReturnSavedCard() {
        String imageUrl = "https://cloudinary.com/image.jpg";
        Card expectedCard = Card.builder().name("Lightning Bolt").build();

        when(cardRepository.save(any(Card.class))).thenReturn(expectedCard);

        Card result = cardServiceHelper.getSavedCard(cardRequest, imageUrl, testUser);

        assertNotNull(result);
        assertEquals(expectedCard, result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void when_cloudinaryManagement_shouldNotProcess_WhenImageIsNull() {
        CardRequest requestWithNullImage = new CardRequest(
                "Test", CardType.INSTANT, "Test", 1, Set.of(ManaColor.RED), "Test",
                0, 0, 0, "Test", 1, "Test", "Test", null, Set.of(Legality.STANDARD), 1
        );

        cardServiceHelper.cloudinaryManagement(requestWithNullImage, testCard);

        verifyNoInteractions(cloudinaryService);
    }

    @Test
    void when_cloudinaryManagement_shouldNotProcess_WhenImageIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        CardRequest requestWithEmptyImage = new CardRequest(
                "Test", CardType.INSTANT, "Test", 1, Set.of(ManaColor.RED), "Test",
                0, 0, 0, "Test", 1, "Test", "Test", emptyFile, Set.of(Legality.STANDARD), 1
        );

        cardServiceHelper.cloudinaryManagement(requestWithEmptyImage, testCard);

        verifyNoInteractions(cloudinaryService);
    }

    @Test
    void when_getPublicIdCloudinary_should_ExtractPublicId() {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg";

        String result = CardServiceHelper.getPublicIdCloudinary(imageUrl);

        assertEquals("sample", result);
    }

    @Test
    void when_getPublicIdCloudinary_should_handleUrlWithoutVersion() {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg";

        String result = CardServiceHelper.getPublicIdCloudinary(imageUrl);

        assertEquals("sample", result);
    }

    @Test
    void when_postImageCloudinary_shouldUpdateImageUrl_whenSuccessful() throws Exception {
        Map<String, Object> uploadResult = Map.of("secure_url", "https://cloudinary.com/new-image.jpg");
        when(cloudinaryService.uploadFile(cardRequest.image())).thenReturn(uploadResult);

        cardServiceHelper.postImageCloudinary(cardRequest, testCard);

        assertEquals("https://cloudinary.com/new-image.jpg", testCard.getImageUrl());
        verify(cloudinaryService).uploadFile(cardRequest.image());
    }

    @Test
    void when_postImageCloudinary_shouldUseDefaultImage_whenCloudinaryFails() throws Exception {
        when(cloudinaryService.uploadFile(cardRequest.image())).thenThrow(new RuntimeException("Cloudinary error"));

        cardServiceHelper.postImageCloudinary(cardRequest, testCard);

        assertEquals("http://localhost:8080/images/dream-logo.png", testCard.getImageUrl());
        verify(cloudinaryService).uploadFile(cardRequest.image());
    }

    @Test
    void when_deleteImageCloudinary_should_callCloudinaryService() throws IOException {
        String publicId = "test_public_id";
        doNothing().when(cloudinaryService).deleteFile(publicId);

        assertDoesNotThrow(() -> cardServiceHelper.deleteImageCloudinary(publicId));

        verify(cloudinaryService).deleteFile(publicId);
    }

    @Test
    void when_deleteImageCloudinary_shouldThrowException_whenCloudinaryFails() throws IOException {
        String publicId = "test_public_id";
        doThrow(new IOException("Delete failed")).when(cloudinaryService).deleteFile(publicId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardServiceHelper.deleteImageCloudinary(publicId));

        assertTrue(exception.getMessage().contains("Error deleting image from Cloudinary"));
        verify(cloudinaryService).deleteFile(publicId);
    }

    @Test
    void when_updatePartOfCard_should_updateCardFields() {
        Card newCard = new Card();
        Card existingCard = Card.builder()
                .id(1L)
                .createdAt(testCard.getCreatedAt())
                .build();

        CardServiceHelper.updatePartOfCard(cardRequest, newCard, existingCard, testUser);

        assertEquals(existingCard.getId(), newCard.getId());
        assertEquals(existingCard.getCreatedAt(), newCard.getCreatedAt());
        assertEquals(testUser, newCard.getUser());
        assertEquals(cardRequest.quantity(), newCard.getQuantity());
    }

    @Test
    void when_updatePartOfCard_shouldSetQuantityToOne_whenNegative() {
        Card newCard = new Card();
        Card existingCard = Card.builder().id(1L).build();

        CardRequest requestWithNegativeQuantity = new CardRequest(
                "Test", CardType.INSTANT, "Test", 1, Set.of(ManaColor.RED), "Test",
                0, 0, 0, "Test", 1, "Test", "Test", null, Set.of(Legality.STANDARD), -1
        );

        CardServiceHelper.updatePartOfCard(requestWithNegativeQuantity, newCard, existingCard, testUser);

        assertEquals(1, newCard.getQuantity());
    }
}
