package com.MagicTheGathering.card;

import com.MagicTheGathering.card.dto.CardMapperDto;
import com.MagicTheGathering.card.dto.CardRequest;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardRepository CARD_REPOSITORY;
    private final UserService USER_SERVICE;

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

    @Transactional(readOnly = true)
    public CardResponse createCard(CardRequest cardRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        CardResponse cardResponse;
        Card newCard = CardMapperDto.toEntity(cardRequest);
        newCard.setUser(user);
        Card savedCard = CARD_REPOSITORY.save(newCard);
        cardResponse = CardMapperDto.fromEntity(savedCard);
        return cardResponse;
    }
}
