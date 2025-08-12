package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCartId.DeckCardId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "deck_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckCard {

    @EmbeddedId
    private DeckCardId id = new DeckCardId();

    @ManyToOne
    @MapsId("deckId")
    @JoinColumn(name = "id_deck")
    private Deck deck;

    @ManyToOne
    @MapsId("cardId")
    @JoinColumn(name = "id_card")
    private Card card;

    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeckCard)) return false;
        DeckCard deckCard = (DeckCard) o;
        return Objects.equals(id, deckCard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
