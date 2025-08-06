package com.MagicTheGathering.card;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardRepository CARD_REPOSITORY;
    private final UserService USER_SERVICE;
    private final UserSecurityUtils USER_SECURITY_UTILS;
    private final CloudinaryService CLOUDINARY_SERVICE;

    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCardsByUser(int page, int size) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = CARD_REPOSITORY.findByUser(user, pageable);
        return cards.map(CardMapperDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public CardResponse getCardById(Long id) {
        Card card = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new RuntimeException("Error"));
        return CardMapperDto.fromEntity(card);
    }

    public CardResponse createCard(CardRequest cardRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        CardResponse cardResponse;

        try {
            Map uploadResult = CLOUDINARY_SERVICE.uploadFile(cardRequest.image());
            String imageUrl = (String) uploadResult.get("secure_url");
            Card newCard = CardMapperDto.toEntity(cardRequest, imageUrl);
            newCard.setUser(user);
            Card savedCard = CARD_REPOSITORY.save(newCard);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        } catch (IOException e) {
            String imageUrl = "http://localhost:8080/images/dream-logo.png";
            Card newCard = CardMapperDto.toEntity(cardRequest, imageUrl);
            newCard.setUser(user);
            Card savedCard = CARD_REPOSITORY.save(newCard);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        }
        return cardResponse;
    }

    public CardResponse updateCard(Long id, CardRequest cardRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Card cardIsExisting = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new RuntimeException("Error"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModify(cardIsExisting)){
            throw new RuntimeException("Unauthorized");
        }

        if (cardRequest.image() != null && !cardRequest.image().isEmpty()) {
            deleteImageCloudinary(cardIsExisting.getImageUrl());
            postImageCloudinary(cardRequest, cardIsExisting);
        }

        Card newCard = CardMapperDto.toEntity(cardRequest, cardIsExisting.getImageUrl());

        int quantity = cardRequest.quantity() >= 0? cardRequest.quantity() : 1;
        newCard.setId(cardIsExisting.getId());
        newCard.setCreatedAt(cardIsExisting.getCreatedAt());
        newCard.setUser(user);
        newCard.setQuantity(quantity);
        return CardMapperDto.fromEntity(newCard);
    }

    public void deleteCard(Long id){
        User user = USER_SERVICE.getAuthenticatedUser();
        Card cardIsExisting = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new RuntimeException("not found id"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModify(cardIsExisting)){
            throw new RuntimeException("Unauthorized");
        }

        String imageUrl = cardIsExisting.getImageUrl();

        String withoutPrefix = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
        if (withoutPrefix.matches("v\\d+/.+")) {
            withoutPrefix = withoutPrefix.substring(withoutPrefix.indexOf('/') + 1);
        }
        int dotIndex = withoutPrefix.lastIndexOf('.');
        String publicId = (dotIndex != -1) ? withoutPrefix.substring(0, dotIndex) : withoutPrefix;

        deleteImageCloudinary(publicId);
        CARD_REPOSITORY.delete(cardIsExisting);
    }


    private void postImageCloudinary(CardRequest request, Card card) {
        try {
            Map uploadResult = CLOUDINARY_SERVICE.uploadFile(request.image());
            String imageUrl = (String) uploadResult.get("secure_url");
            card.setImageUrl(imageUrl);
        } catch (Exception e) {
            card.setImageUrl("http://localhost:8080/images/dream-logo.png");
            System.out.println("Fallo Cloudinary, usando imagen por defecto: " + card.getImageUrl());
        }
    }

    private void deleteImageCloudinary(String publicId) {
        try {
            CLOUDINARY_SERVICE.deleteFile(publicId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from Cloudinary: " + e.getMessage());
        }
    }
}
