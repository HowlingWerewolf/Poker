package com.game.playground.asset;

import lombok.Builder;

@Builder
public record Card(Color color, Value value) implements Comparable<Card> {

    @Override
    public int compareTo(final Card o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || other.getClass() != Card.class) {
            return false;
        }

        final Card otherCard = (Card) other;
        return this.isSameValue(otherCard) && this.isSameColor(otherCard);
    }

    @Override
    public String toString() {
        return color.name() + " of " + value.toString();
    }

    public boolean isSameColor(final Card otherCard) {
        return this.color().equals(otherCard.color());
    }

    public boolean isSameValue(final Card otherCard) {
        return this.value().equals(otherCard.value());
    }

    @Override
    public Card clone() throws CloneNotSupportedException {
        return (Card) super.clone();
    }

}
