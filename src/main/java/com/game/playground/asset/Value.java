package com.game.playground.asset;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Value {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14),
    ;

    public final Integer index;

    Value(int i) {
        this.index = i;
    }

    public static Value indexToValue(final int i) {
        return Arrays.stream(values())
                .filter(value -> value.getIndex().equals(i)).findFirst().orElse(null);
    }
}
