package com.MagicTheGathering.deck;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardService;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.deck.dto.DeckMapperDto;
import com.MagicTheGathering.deck.dto.DeckRequest;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.utils.DeckServiceHelper;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.deckCard.DeckCardRepository;
import com.MagicTheGathering.deckCard.DeckCardService;
import com.MagicTheGathering.deckCartId.DeckCardId;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<DeckResponse> getAllDeckByUser(int page, int size) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Deck> decks = DECK_REPOSITORY.findByUser(user, pageable);
        return decks.map(DeckMapperDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DeckResponse> getAllPublicDecks(int page, int size){
        User user = USER_SERVICE.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Deck> decks = DECK_REPOSITORY.findByIsPublicTrue(pageable);
        return decks.map(DeckMapperDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public DeckResponse getDeckById(Long id) {
        Deck deck = DECK_REPOSITORY.findById(id)
                .orElseThrow(() -> new RuntimeException("deck not found"));

        User currentUser = USER_SERVICE.getAuthenticatedUser();

        if (!deck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new RuntimeException("unauthorized");
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
                .orElseThrow(() -> new RuntimeException("deck not found"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(existingDeck)){
            throw new RuntimeException("unauthorized");
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
                .orElseThrow(() -> new RuntimeException("deck not found"));

        if (!existingDeck.getIsPublic() && !USER_SECURITY_UTILS.isAuthorizedToModifyDeck(existingDeck)){
            throw new RuntimeException("unauthorized");
        }

        DECK_REPOSITORY.delete(existingDeck);
    }

    public DeckResponse addCardToDeck(Long deckId, AddCardDeckRequest request){
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new RuntimeException("deck not found"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new RuntimeException("unauthorized");
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
                .orElseThrow(() -> new RuntimeException("deck not found"));

        return DeckMapperDto.fromEntity(updatedDeck);
    }

    public DeckResponse removeCardFromDeck(Long deckId, Long cardId, int quantityToRemove) {
        User user = USER_SERVICE.getAuthenticatedUser();
        Deck deck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new RuntimeException("deck not found"));

        if (!USER_SECURITY_UTILS.isAuthorizedToModifyDeck(deck)){
            throw new RuntimeException("unauthorized");
        }

        DeckCardId deckCardId = new DeckCardId(deckId, cardId);
        DeckCard deckCard = DECK_CARD_REPOSITORY.findById(deckCardId)
                .orElseThrow(() -> new RuntimeException("card not found in deck"));

        if (deckCard.getQuantity() <= quantityToRemove){
            DECK_CARD_REPOSITORY.delete(deckCard);
        } else {
            deckCard.setQuantity(deckCard.getQuantity() - quantityToRemove);
             DECK_CARD_REPOSITORY.save(deckCard);

        }

        Deck updatedDeck = DECK_REPOSITORY.findById(deckId)
                .orElseThrow(() -> new RuntimeException("deck not found"));

        return DeckMapperDto.fromEntity(updatedDeck);
    }
}
