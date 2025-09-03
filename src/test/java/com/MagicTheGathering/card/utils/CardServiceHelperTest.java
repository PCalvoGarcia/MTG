package com.MagicTheGathering.card.utils;


import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    void when_getCardResponseList_then_return_list() {
        testCard.setUser(testUser);
        List<Card> cardResponseList = new ArrayList<>();
        cardResponseList.add(testCard);
        cardResponseList.add(testCard);

        when(cardRepository.findByUser(testUser)).thenReturn(cardResponseList);

        List<CardResponse> responses = cardServiceHelper.getCardResponseList(testUser);

        assertEquals(1L, responses.getFirst().id());
        assertEquals(2, responses.size());

        verify(cardRepository).findByUser(testUser);
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
    void when_cloudinaryManagement_shouldProcess_WhenImageIsNotEmpty() throws IOException {
        String imageUrl = "https://cloudinary.com/test/image.jpg";

        cardServiceHelper.cloudinaryManagement(cardRequest, testCard);

        verify(cloudinaryService).deleteFile(imageUrl);
        verify(cloudinaryService).uploadFile(cardRequest.image());
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
    void when_getCardWithCloudinary_Success() throws IOException {
        Map<String, Object> uploadResult = new HashMap<>();
        String expectedImageUrl = "https://cloudinary.com/secure/uploaded-image.jpg";
        uploadResult.put("secure_url", expectedImageUrl);

        Card savedCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .imageUrl(expectedImageUrl)
                .user(testUser)
                .build();

        Card responseCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .imageUrl(expectedImageUrl)
                .user(testUser)
                .build();

        CardResponse expectedResponse = CardMapperDto.fromEntity(responseCard);

        when(cloudinaryService.uploadFile(cardRequest.image())).thenReturn(uploadResult);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        try (MockedStatic<CardMapperDto> mockedMapper = mockStatic(CardMapperDto.class)) {
            mockedMapper.when(() -> CardMapperDto.toEntity(eq(cardRequest), eq(expectedImageUrl)))
                    .thenReturn(testCard);
            mockedMapper.when(() -> CardMapperDto.fromEntity(savedCard))
                    .thenReturn(expectedResponse);

            CardResponse result = cardServiceHelper.getCardWithCloudinary(cardRequest, testUser);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Lightning Bolt");
            assertThat(result.imageUrl()).isEqualTo(expectedImageUrl);

            verify(cloudinaryService, times(1)).uploadFile(cardRequest.image());
            verify(cardRepository, times(1)).save(any(Card.class));
            mockedMapper.verify(() -> CardMapperDto.toEntity(eq(cardRequest), eq(expectedImageUrl)), times(1));
            mockedMapper.verify(() -> CardMapperDto.fromEntity(savedCard), times(1));
        }
    }

    @Test
    void when_gwtCardWithCloudinary_CloudinaryFailure() throws IOException {
        String defaultImageUrl = "http://localhost:8080/images/dream-logo.png";

        Card savedCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .imageUrl(defaultImageUrl)
                .user(testUser)
                .build();
        Card responseCard = Card.builder()
                .id(1L)
                .name("Lightning Bolt")
                .imageUrl(defaultImageUrl)
                .user(testUser)
                .build();
        CardResponse expectedResponse = CardMapperDto.fromEntity(responseCard);


        when(cloudinaryService.uploadFile(cardRequest.image())).thenThrow(new IOException("Upload failed"));
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        try (MockedStatic<CardMapperDto> mockedMapper = mockStatic(CardMapperDto.class)) {
            mockedMapper.when(() -> CardMapperDto.toEntity(eq(cardRequest), eq(defaultImageUrl)))
                    .thenReturn(testCard);
            mockedMapper.when(() -> CardMapperDto.fromEntity(savedCard))
                    .thenReturn(expectedResponse);

            CardResponse result = cardServiceHelper.getCardWithCloudinary(cardRequest, testUser);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Lightning Bolt");
            assertThat(result.imageUrl()).isEqualTo(defaultImageUrl);

            verify(cloudinaryService, times(1)).uploadFile(cardRequest.image());
            verify(cardRepository, times(1)).save(any(Card.class));
            mockedMapper.verify(() -> CardMapperDto.toEntity(eq(cardRequest), eq(defaultImageUrl)), times(1));
            mockedMapper.verify(() -> CardMapperDto.fromEntity(savedCard), times(1));
        }
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
