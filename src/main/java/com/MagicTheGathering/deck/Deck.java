package com.MagicTheGathering.deck;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "decks")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Boolean isPublic;

    @ElementCollection(targetClass = CardType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "type_card", joinColumns = @JoinColumn(name = "card_id"))
    @Enumerated(EnumType.STRING)
    private Set<CardType> types = new HashSet<>();

    @Column(name = "max_cards")
    private int maxCards;

    @ManyToMany
    @JoinTable(
            name = "deck_card",
            joinColumns = @JoinColumn(name = "id_deck"),
            inverseJoinColumns = @JoinColumn(name = "id_card")
    )
    private Set<Card> cards = new HashSet<>();

    @Column(name = "deck_name")
    private String deckName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt= LocalDateTime.now();
    }

}
