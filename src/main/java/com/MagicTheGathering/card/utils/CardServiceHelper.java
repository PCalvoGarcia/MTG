package com.MagicTheGathering.card.utils;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CardServiceHelper {

    private final CardRepository CARD_REPOSITORY;
    private final CloudinaryService CLOUDINARY_SERVICE;

    public Card getSavedCard(CardRequest cardRequest, String imageUrl, User user) {
        Card newCard = CardMapperDto.toEntity(cardRequest, imageUrl);
        newCard.setUser(user);
        Card savedCard = CARD_REPOSITORY.save(newCard);
        return savedCard;
    }

    public List<CardResponse> getCardResponseList(User user) {
        List<CardResponse> cards = CARD_REPOSITORY.findByUser(user)
                .stream()
                .map(CardMapperDto::fromEntity)
                .collect(Collectors.toList());
        return cards;
    }

    public void cloudinaryManagement(CardRequest cardRequest, Card cardIsExisting) {
        if (cardRequest.image() != null && !cardRequest.image().isEmpty()) {
            deleteImageCloudinary(cardIsExisting.getImageUrl());
            postImageCloudinary(cardRequest, cardIsExisting);
        }
    }

    public static String getPublicIdCloudinary(String imageUrl) {
        String withoutPrefix = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
        if (withoutPrefix.matches("v\\d+/.+")) {
            withoutPrefix = withoutPrefix.substring(withoutPrefix.indexOf('/') + 1);
        }
        int dotIndex = withoutPrefix.lastIndexOf('.');
        String publicId = (dotIndex != -1) ? withoutPrefix.substring(0, dotIndex) : withoutPrefix;
        return publicId;
    }

    public void postImageCloudinary(CardRequest request, Card card) {
        try {
            Map uploadResult = CLOUDINARY_SERVICE.uploadFile(request.image());
            String imageUrl = (String) uploadResult.get("secure_url");
            card.setImageUrl(imageUrl);
        } catch (Exception e) {
            card.setImageUrl("http://localhost:8080/images/dream-logo.png");
        }
    }

    public void deleteImageCloudinary(String publicId) {
        try {
            CLOUDINARY_SERVICE.deleteFile(publicId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from Cloudinary: " + e.getMessage());
        }
    }

    public CardResponse getCardWithCloudinary(CardRequest cardRequest, User user) {
        CardResponse cardResponse;
        try {
            Map uploadResult = CLOUDINARY_SERVICE.uploadFile(cardRequest.image());
            String imageUrl = (String) uploadResult.get("secure_url");
            Card savedCard = getSavedCard(cardRequest, imageUrl, user);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        } catch (IOException e) {
            String imageUrl = "http://localhost:8080/images/dream-logo.png";
            Card savedCard = getSavedCard(cardRequest, imageUrl, user);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        }
        return cardResponse;
    }

    public static void updatePartOfCard(CardRequest cardRequest, Card newCard, Card cardIsExisting, User user) {
        int quantity = cardRequest.quantity() >= 0? cardRequest.quantity() : 1;
        newCard.setId(cardIsExisting.getId());
        newCard.setCreatedAt(cardIsExisting.getCreatedAt());
        newCard.setUser(user);
        newCard.setQuantity(quantity);
    }
}
