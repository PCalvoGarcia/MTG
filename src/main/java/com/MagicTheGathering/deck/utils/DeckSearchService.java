package com.MagicTheGathering.deck.utils;

import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.DeckRepository;
import com.MagicTheGathering.deck.dto.DeckMapperDto;
import com.MagicTheGathering.deck.dto.DeckResponse;
import com.MagicTheGathering.deck.exceptions.InvalidFormatsException;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserRepository;
import com.MagicTheGathering.user.exceptions.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeckSearchService {

    private final DeckRepository DECK_REPOSITORY;
    private final UserRepository USER_REPOSITORY;

    public Page<DeckResponse> getDecksByFormat(String format, Pageable pageable){
        try {
            Legality legality = Legality.valueOf(format.toUpperCase());
            Page<Deck> decks = DECK_REPOSITORY.findByTypeAndIsPublicTrue(legality, pageable);
            return decks.map(DeckMapperDto::fromEntity);
        } catch (IllegalArgumentException e){
            throw new InvalidFormatsException( format, java.util.Arrays.toString(Legality.values()));
        }
    }

    public Page<DeckResponse> getPublicDecksByUser(Long userId, Pageable pageable){
        User user = USER_REPOSITORY.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException(userId));

        Page<Deck> decks = DECK_REPOSITORY.findByUserAndIsPublicTrue(user, pageable);
        return decks.map(DeckMapperDto::fromEntity);
    }

}
