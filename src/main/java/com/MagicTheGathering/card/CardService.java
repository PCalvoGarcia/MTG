package com.MagicTheGathering.card;

import com.MagicTheGathering.Cloudinary.CloudinaryService;
import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.card.exceptions.CardIdNotFoundException;
import com.MagicTheGathering.card.exceptions.DeleteCardNotAllowedException;
import com.MagicTheGathering.card.utils.CardServiceHelper;
import com.MagicTheGathering.deckCard.DeckCardRepository;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository CARD_REPOSITORY;
    private final UserService USER_SERVICE;
    private final UserSecurityUtils USER_SECURITY_UTILS;
    private final CardServiceHelper CARD_SERVICE_HELPER;
    private final CloudinaryService CLOUDINARY_SERVICE;
    private final DeckCardRepository DECK_CARD_REPOSITORY;

    @Transactional(readOnly = true)
    public List<CardResponse> getAllCardsByUser() {
        User user = USER_SERVICE.getAuthenticatedUser();
        List<CardResponse> cards = CARD_REPOSITORY.findByUser(user)
                .stream()
                .map(CardMapperDto::fromEntity)
                .collect(Collectors.toList());
        return cards;
    }

    @Transactional(readOnly = true)
    public CardResponse getCardById(Long id) {
        Card card = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new CardIdNotFoundException(id));
        return CardMapperDto.fromEntity(card);
    }

    public CardResponse createCard(CardRequest cardRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        CardResponse cardResponse;
        try {
            Map uploadResult = CLOUDINARY_SERVICE.uploadFile(cardRequest.image());
            String imageUrl = (String) uploadResult.get("secure_url");
            Card savedCard = CARD_SERVICE_HELPER.getSavedCard(cardRequest, imageUrl, user);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        } catch (IOException e) {
            String imageUrl = "http://localhost:8080/images/dream-logo.png";
            Card savedCard = CARD_SERVICE_HELPER.getSavedCard(cardRequest, imageUrl, user);
            cardResponse = CardMapperDto.fromEntity(savedCard);
        }
        return cardResponse;
    }

    public CardResponse updateCard(Long id, CardRequest cardRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Card cardIsExisting = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new CardIdNotFoundException(id));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyCard(cardIsExisting)){
            throw new UnauthorizedModificationsException();
        }

        CARD_SERVICE_HELPER.cloudinaryManagement(cardRequest, cardIsExisting);

        Card newCard = CardMapperDto.toEntity(cardRequest, cardIsExisting.getImageUrl());

        CARD_SERVICE_HELPER.updatePartOfCard(cardRequest, newCard, cardIsExisting, user);
        return CardMapperDto.fromEntity(newCard);
    }

    public void deleteCard(Long id){
        User user = USER_SERVICE.getAuthenticatedUser();
        Card cardIsExisting = CARD_REPOSITORY.findById(id)
                .orElseThrow(() -> new CardIdNotFoundException(id));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyCard(cardIsExisting)){
            throw new UnauthorizedModificationsException();
        }

        if (DECK_CARD_REPOSITORY.existsByCard(cardIsExisting)) {
            throw new DeleteCardNotAllowedException();
        }
        String imageUrl = cardIsExisting.getImageUrl();

        String publicId = CARD_SERVICE_HELPER.getPublicIdCloudinary(imageUrl);

        CARD_SERVICE_HELPER.deleteImageCloudinary(publicId);
        CARD_REPOSITORY.delete(cardIsExisting);
    }
}
