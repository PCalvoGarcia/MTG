package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deckCartId.DeckCardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCard, Long> {
    Optional<DeckCard> findById(DeckCardId deckCardId);

    @Query("SELECT dc FROM DeckCard dc WHERE dc.deck.id = :deckId")
    List<DeckCard> findByDeckId(@Param("deckId") Long deckId);

    List<DeckCard> findByCardId(Long cardId);

    boolean existsByCard(Card cardIsExisting);

}
