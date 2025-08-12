package com.MagicTheGathering.legality;

public enum Legality {
    STANDARD(60),
    MODERN(60),
    COMMANDER(100),
    PIONEER(60),
    LEGACY_VINTAGE(60),
    PAUPER(60),
    BRAWL(60),
    BOOSTER_DRAFT(40),
    SEALED_DECK(40);

    private final int maxCards;

    Legality(int maxCards) {
        this.maxCards = maxCards;
    }

    public int getMaxCards() {
        return maxCards;
    }
}
