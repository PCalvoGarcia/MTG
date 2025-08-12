package com.MagicTheGathering.deck;

import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "decks")
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

    @Enumerated(EnumType.STRING)
    private Legality type;

    @Column(name = "max_cards")
    private int maxCards;


    @Column(name = "deck_name")
    private String deckName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<DeckCard> deckCards = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        createdAt= LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deck)) return false;
        Deck deck = (Deck) o;
        return Objects.equals(id, deck.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
