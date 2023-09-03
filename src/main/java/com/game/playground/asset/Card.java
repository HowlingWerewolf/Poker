package com.game.playground.asset;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class Card implements Comparable<Card>{
    final Color color;
    final Value value;

    @Override
    public int compareTo(Card o) {
        return this.getValue().getIndex().compareTo(o.getValue().getIndex());
    }
}
