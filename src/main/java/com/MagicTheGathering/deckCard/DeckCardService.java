package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deckCard.dto.DeckCardMapperDto;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class DeckCardService {

    private final DeckCardRepository DECK_CARD_REPOSITORY;
    private final DeckRepository DECK_REPOSITORY;
    private final UserService USER_SERVICE;
    private final UserSecurityUtils USER_SECURITY_UTILS;

    public DeckCard getExistingDeckCard(DeckCardId deckCardId) {
        return DECK_CARD_REPOSITORY.findById(deckCardId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DeckCardResponse> getCardsByDeckId(Long deckId) {
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)) {
            throw new RuntimeException("Access denied to private deck");
        }

        List<DeckCard> deckCardsCopy = new ArrayList<>(deck.getDeckCards());

        return deckCardsCopy.stream()
                .map(DeckCardMapperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeckCardResponse getDeckCard(Long deckId, Long cardId) {
        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new RuntimeException("Card not found in deck"));

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deckCard.getDeck().getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new RuntimeException("Access denied");
        }

        return DeckCardMapperDto.fromEntity(deckCard);
    }

    public DeckCardResponse updateDeckCardQuantity(Long deckId, Long cardId, int newQuantity) {
        User user = USER_SERVICE.getAuthenticatedUser();

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new RuntimeException("Card not found in deck"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new RuntimeException("Unauthorized to modify this deck");
        }

        if (newQuantity <= 0) {
            DECK_CARD_REPOSITORY.delete(deckCard);
            return null;
        }

        if (newQuantity > 4) {
            throw new RuntimeException("Maximum 4 copies of a card allowed");
        }

        deckCard.setQuantity(newQuantity);
        DeckCard updatedDeckCard = DECK_CARD_REPOSITORY.save(deckCard);

        return DeckCardMapperDto.fromEntity(updatedDeckCard);
    }

    public void removeDeckCard(Long deckId, Long cardId) {
        User user = USER_SERVICE.getAuthenticatedUser();

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new RuntimeException("Card not found in deck"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new RuntimeException("Unauthorized to modify this deck");
        }

        DECK_CARD_REPOSITORY.delete(deckCard);
    }

}
