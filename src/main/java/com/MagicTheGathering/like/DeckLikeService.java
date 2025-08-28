package com.MagicTheGathering.like;

import com.MagicTheGathering.Exceptions.DeckIdNotFoundException;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deckCard.exceptions.AccessDeniedPrivateDeckException;
import com.MagicTheGathering.like.dto.DeckLikeMapperDto;
import com.MagicTheGathering.like.dto.DeckLikeResponse;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeckLikeService {

    private final DeckLikeRepository deckLikeRepository;
    private final DeckRepository deckRepository;
    private final UserService userService;

    @Transactional
    public DeckLikeResponse manageLike(Long deckId) {
        User user = userService.getAuthenticatedUser();
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));

        if (!deck.getIsPublic()){
            throw new AccessDeniedPrivateDeckException();
        }

        Optional<DeckLike> optionalDeckLike = deckLikeRepository.findByUserAndDeck(user, deck);

        if (optionalDeckLike.isPresent()) {
            deckLikeRepository.delete(optionalDeckLike.get());
        } else {
            DeckLike deckLike = DeckLike.builder()
                    .deck(deck)
                    .user(user)
                    .build();
            deckLikeRepository.save(deckLike);
        }

        boolean liked = deckLikeRepository.existsByUserAndDeck(user, deck);
        long count = deckLikeRepository.countByDeck(deck);

        return DeckLikeMapperDto.fromEntity(deck, liked, count);
    }

    public DeckLikeResponse getLikesByDeckId(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckIdNotFoundException(deckId));
        User user = userService.getAuthenticatedUser();
        boolean liked = deckLikeRepository.existsByUserAndDeck(user, deck);


        long count = deckLikeRepository.countByDeck(deck);

        return DeckLikeMapperDto.fromEntity(deck, liked, count);
    }

    public List<DeckLike> getDeckLikedByUser(User user) {
        List<DeckLike> deckList = deckLikeRepository.findByUser(user);
        return deckList;
    }

    public void deleteLikesByDeckId(Long deckId) {
        deckLikeRepository.deleteByDeckId(deckId);
    }
}
