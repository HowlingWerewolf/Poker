package com.game.elements;

import lombok.Getter;

@Getter
public enum Ranking {
    HIGH_CARD(0),
    ONE_PAIR(1),
    TWO_PAIRS(2),
    DRILL(3),
    STRAIGHT(4),
    FLUSH(5),
    FULL_HOUSE(6),
    POKER(7),
    STRAIGHT_FLUSH(8),
    ROYAL_FLUSH(9),
    ;

    public final Integer strength;

    Ranking(int i) {
        this.strength = i;
    }
}
