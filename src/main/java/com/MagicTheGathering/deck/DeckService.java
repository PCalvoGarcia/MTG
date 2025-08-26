package com.MagicTheGathering.deck;

import com.MagicTheGathering.Exceptions.UnauthorizedModificationsException;
import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckMapperDto;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckServiceHelper;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCard.DeckCardRepository;
import com.MagicTheGathering.deckCard.DeckCardService;
import com.MagicTheGathering.deckCard.exceptions.CardIdNotFoundInDeckException;
import com.MagicTheGathering.deckCard.exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class DeckService {
    private final DeckRepository DECK_REPOSITORY;
    private final DeckCardRepository DECK_CARD_REPOSITORY;
    private final UserService USER_SERVICE;
    private final UserSecurityUtils USER_SERVICE_UTILS;
    private final UserSecurityUtils USER_SECURITY_UTILS;
    private final DeckServiceHelper DECK_SERVICE_HELPER;
    private final DeckCardService DECK_CARD_SERVICE;

    @Transactional(readOnly = true)
    public List<DeckResponse> getAllDeckByUser() {
        User user = USER_SERVICE.getAuthenticatedUser();
        List<Deck> decks = DECK_REPOSITORY.findByUser(user);
        return decks.stream()
                .map(DeckMapperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeckResponse> getAllPublicDecks(){
        User user = USER_SERVICE.getAuthenticatedUser();
        List<Deck> decks = DECK_REPOSITORY.findByIsPublicTrue();
        return decks.stream()
                .map(DeckMapperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeckResponse getDeckById(Long id) {
        Deck deck = DECK_REPOSITORY.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new RuntimeException("Unauthorized");
        }

        return DeckMapperDto.fromEntity(deck);
    }

    public DeckResponse createDeck(DeckRequest deckRequest) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck newDeck = DeckMapperDto.toEntity(deckRequest, user);
        Deck savedDeck = DECK_REPOSITORY.save(newDeck);

        return DeckMapperDto.fromEntity(savedDeck);
    }

    public DeckResponse updateDeck(Long id, DeckRequest deckRequest){
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck existingDeck = DECK_REPOSITORY.findById(id)
                .orElseThrow(() -> new DeckIdNotFoundException(id));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(existingDeck)){
            throw new UnauthorizedModificationsException();
        }

        existingDeck.setDeckName(deckRequest.deckName());
        existingDeck.setIsPublic(deckRequest.isPublic());
        existingDeck.setType(deckRequest.legalityEnum());
        existingDeck.setMaxCards(deckRequest.legalityEnum().getMaxCards());

        return DeckMapperDto.fromEntity(existingDeck);
    }

    public void deleteDeck(Long id){
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck existingDeck = DECK_REPOSITORY.findById(id)
                .orElseThrow(() -> new DeckIdNotFoundException(id));

        if (!existingDeck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(existingDeck)){
            throw new UnauthorizedModificationsException();
        }

        DECK_REPOSITORY.delete(existingDeck);
    }

    public DeckResponse addCardToDeck(Long deckId, AddCardDeckRequest request){
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new UnauthorizedModificationsException();
        }

        Card card = USER_SERVICE_UTILS.findCardById(request);

        DECK_SERVICE_HELPER.validateCardAddition(deck, card, request.quantity());

        DeckCardId deckCardId = new DeckCardId(deckId, request.cardId());
        DeckCard existingDeckCard = DECK_CARD_SERVICE.getExistingDeckCard(deckCardId);

        if (!(existingDeckCard == null)) {
            int newQuantity = existingDeckCard.getQuantity() + request.quantity();
            DECK_SERVICE_HELPER.validateMaxCopiesLand(card, newQuantity);
            existingDeckCard.setQuantity(newQuantity);
            DECK_CARD_REPOSITORY.save(existingDeckCard);
        } else {
            DeckCard newDeckCard = new DeckCard();
            newDeckCard.setId(deckCardId);
            newDeckCard.setDeck(deck);
            newDeckCard.setCard(card);
            newDeckCard.setQuantity(request.quantity());
            DECK_CARD_REPOSITORY.save(newDeckCard);
        }

        Deck updatedDeck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        return DeckMapperDto.fromEntity(updatedDeck);
    }

    public DeckResponse removeCardFromDeck(Long deckId, Long cardId, int quantityToRemove) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new UnauthorizedModificationsException();
        }

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new CardIdNotFoundInDeckException());

        if (deckCard.getQuantity() <= quantityToRemove){
            DECK_CARD_REPOSITORY.delete(deckCard);
        } else {
            deckCard.setQuantity(deckCard.getQuantity() - quantityToRemove);
             DECK_CARD_REPOSITORY.save(deckCard);

        }

        Deck updatedDeck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        return DeckMapperDto.fromEntity(updatedDeck);
    }
}
