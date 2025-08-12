package com.MagicTheGathering.deckCartId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckCardId implements Serializable {

    @Column(name = "id_deck")
    private Long deckId;

    @Column (name = "id_card")
    private Long cardId;
}
