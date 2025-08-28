package com.MagicTheGathering.like;

import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckLikeRepository extends JpaRepository<DeckLike, Long> {
    Optional<DeckLike> findByUserAndDeck(User user, Deck deck);
    List<DeckLike> findByUser(User user);
    boolean existsByUserAndDeck(User user, Deck deck);
    long countByDeck(Deck deck);

    @Transactional
    void deleteByDeckId(Long deckId);
}
