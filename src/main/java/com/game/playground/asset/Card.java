package com.game.playground.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Card implements Comparable<Card> {
    final Color color;
    final Value value;

    @Override
    public int compareTo(Card o) {
        return this.getValue().getIndex().compareTo(o.getValue().getIndex());
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || other.getClass() != Card.class) {
            return false;
        }

        final Card otherCard = (Card) other;
        return this.isSameColor(otherCard) && this.isSameValue(otherCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }

    @Override
    public String toString() {
        return color.name() + " of " + value.toString();
    }

    public boolean isSameColor(final Card otherCard) {
        return this.getColor().equals(otherCard.getColor());
    }

    public boolean isSameValue(final Card otherCard) {
        return this.getValue().equals(otherCard.getValue());
    }

}
