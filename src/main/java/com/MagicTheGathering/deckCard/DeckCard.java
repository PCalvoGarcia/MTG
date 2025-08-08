package com.MagicTheGathering.deckCard;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCartId.DeckCardId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deck_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
