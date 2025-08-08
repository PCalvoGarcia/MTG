package com.MagicTheGathering.card;

import com.MagicTheGathering.cardType.CardType;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deckCard.DeckCard;
import com.MagicTheGathering.legality.Legality;
import com.MagicTheGathering.manaColor.ManaColor;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cards")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String name;

    @ElementCollection(targetClass = CardType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "type_card", joinColumns = @JoinColumn(name = "card_id"))
    @Enumerated(EnumType.STRING)
    private Set<CardType> types = new HashSet<>();

    @Column(name = "specific_type")
    private String specificType;

    @Column(name = "mana_total_cost")
    private int manaTotalCost;

    @ElementCollection(targetClass = ManaColor.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "color_card", joinColumns = @JoinColumn(name = "card_id"))
    @Enumerated(EnumType.STRING)
    private Set<ManaColor> manaColors = new HashSet<>();

    @Column(name = "text_rules")
    private String textRules;

    private int power;

    private int endurance;

    private int loyalty;

    private String collection;

    @Column(name = "cart_number")
    private int cartNumber;

    private String artist;

    private String edition;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection(targetClass = Legality.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "legality_card", joinColumns = @JoinColumn(name = "card_id"))
    @Enumerated(EnumType.STRING)
    private Set<Legality> legalityFormat = new HashSet<>();

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "card")
    private Set<DeckCard> deckCards = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt= LocalDateTime.now();
    }

}
