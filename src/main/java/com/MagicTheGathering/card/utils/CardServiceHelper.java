package com.MagicTheGathering.card.utils;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
            System.out.println("Fallo Cloudinary, usando imagen por defecto: " + card.getImageUrl());
        }
    }

    public void deleteImageCloudinary(String publicId) {
        try {
            CLOUDINARY_SERVICE.deleteFile(publicId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from Cloudinary: " + e.getMessage());
        }
    }

    public static void updatePartOfCard(CardRequest cardRequest, Card newCard, Card cardIsExisting, User user) {
        int quantity = cardRequest.quantity() >= 0? cardRequest.quantity() : 1;
        newCard.setId(cardIsExisting.getId());
        newCard.setCreatedAt(cardIsExisting.getCreatedAt());
        newCard.setUser(user);
        newCard.setQuantity(quantity);
    }
}
