package com.MagicTheGathering.deck;

import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findByUser(User user);

    List<Deck> findByIsPublicTrue();

    List<Deck> findByTypeAndIsPublicTrue(Legality legality);

    List<Deck> findByUserAndIsPublicTrue(User user);

}
