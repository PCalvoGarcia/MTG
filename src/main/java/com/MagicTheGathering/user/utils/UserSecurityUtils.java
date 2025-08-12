package com.MagicTheGathering.user.utils;

import com.MagicTheGathering.card.Card;
import com.MagicTheGathering.card.CardRepository;
import com.MagicTheGathering.deck.Deck;
import com.MagicTheGathering.deck.dto.AddCardDeckRequest;
import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserSecurityUtils {

    private final UserService userService;
    private final CardRepository CARD_REPOSITORY;

    public UserSecurityUtils(CardRepository CARD_REPOSITORY, UserService userService) {
        this.CARD_REPOSITORY = CARD_REPOSITORY;
        this.userService = userService;
    }

    public static org.springframework.security.core.userdetails.User createUserByUserDetails(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

    public static List<GrantedAuthority> getAuthoritiesRole(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    public boolean isAuthorizedToModifyCard(Card card) {
        User user = userService.getAuthenticatedUser();
        return card.getUser().getId().equals(user.getId());
    }

    public boolean isAuthorizedToModifyDeck(Deck deck) {
        User user = userService.getAuthenticatedUser();
        return deck.getUser().getId().equals(user.getId());
    }

    public Card findCardById(AddCardDeckRequest request) {
        return CARD_REPOSITORY.findById(request.cardId())
                .orElseThrow(() -> new RuntimeException("card not found"));
    }

}