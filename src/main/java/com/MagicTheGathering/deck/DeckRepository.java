package com.MagicTheGathering.deck;

import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Page<Deck> findByUser(User user, Pageable pageable);

    Page<Deck> findByIsPublicTrue(Pageable pageable);

    Page<Deck> findByTypeAndIsPublicTrue(Legality legality, Pageable pageable);

    Page<Deck> findByUserAndIsPublicTrue(User user, Pageable pageable);

}
