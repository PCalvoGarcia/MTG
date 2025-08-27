package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.Exceptions.UnauthorizedAccessException;
import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.card.CardService;
import com.MagicTheGathering.card.dto.CardResponse;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deckCard.dto.DeckCardMapperDto;
import com.MagicTheGathering.deckCard.dto.DeckCardResponse;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.deckCard.exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCard.exceptions.MaxCopiesAllowedException;
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
    private final CardService CARD_SERVICE;
    private final UserService USER_SERVICE;
    private final UserSecurityUtils USER_SECURITY_UTILS;

    public DeckCard getExistingDeckCard(DeckCardId deckCardId) {
        return DECK_CARD_REPOSITORY.findById(deckCardId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DeckCardResponse> getCardsByDeckId(Long deckId) {
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)) {
            throw new AccessDeniedPrivateDeckException();
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
                .orElseThrow(() -> new CardIdNotFoundInDeckException());

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deckCard.getDeck().getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new UnauthorizedAccessException();
        }

        return DeckCardMapperDto.fromEntity(deckCard);
    }

    public DeckCardResponse updateDeckCardQuantity(Long deckId, Long cardId, int newQuantity) {
        User user = USER_SERVICE.getAuthenticatedUser();

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new CardIdNotFoundInDeckException());

        CardResponse card = CARD_SERVICE.getCardById(cardId);

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new UnauthorizedModificationsException();
        }

        if (newQuantity <= 0) {
            DECK_CARD_REPOSITORY.delete(deckCard);
            return null;
        }

        if (newQuantity > 4 && !card.cardType().contains(CardType.BASIC_LAND)) {
            throw new MaxCopiesAllowedException();
        }

        deckCard.setQuantity(newQuantity);
        DeckCard updatedDeckCard = DECK_CARD_REPOSITORY.save(deckCard);

        return DeckCardMapperDto.fromEntity(updatedDeckCard);
    }

    public void removeDeckCard(Long deckId, Long cardId) {
        User user = USER_SERVICE.getAuthenticatedUser();

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new CardIdNotFoundInDeckException());

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deckCard.getDeck())) {
            throw new UnauthorizedModificationsException();
        }

        DECK_CARD_REPOSITORY.delete(deckCard);
    }

}
