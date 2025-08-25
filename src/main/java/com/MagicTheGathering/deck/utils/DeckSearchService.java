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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeckSearchService {

    private final DeckRepository DECK_REPOSITORY;
    private final UserRepository USER_REPOSITORY;

    public List<DeckResponse> getDecksByFormat(String format){
        try {
            Legality legality = Legality.valueOf(format.toUpperCase());
            List<Deck> decks = DECK_REPOSITORY.findByTypeAndIsPublicTrue(legality);
            return decks.stream()
                    .map(DeckMapperDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e){
            throw new InvalidFormatsException( format, java.util.Arrays.toString(Legality.values()));
        }
    }

    public List<DeckResponse> getPublicDecksByUser(Long userId){
        User user = USER_REPOSITORY.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException(userId));

        List<Deck> decks = DECK_REPOSITORY.findByUserAndIsPublicTrue(user);
        return decks.stream()
                .map(DeckMapperDto::fromEntity)
                .collect(Collectors.toList());
    }

}
