package com.game.elements;

import com.game.playground.asset.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hand implements Comparable<Hand> {

    private List<Card> cards;
    private Ranking ranking;

    @Override
    public int compareTo(final Hand otherHand) {
        // firstly the ranking decides
        if (!this.ranking.getStrength().equals(otherHand.ranking.getStrength())) {
            return this.ranking.getStrength().compareTo(otherHand.ranking.getStrength());
        }

        // ranking is the same
        // secondly each ranking has their own rule for decision
        // TODO

        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != Hand.class) {
            return false;
        }

        final Hand otherHand = (Hand) obj;
        if (this.cards.size() != otherHand.cards.size()) {
            return false;
        }

        for (int i = 0; i < 5; i++) {
            final Card card = this.cards.get(i);
            final Card otherCard = otherHand.getCards().get(i);
            if (!isSameColor(card, otherCard) || !isSameValue(card, otherCard)) {
                return false;
            }
        }

        return true;
    }

    private boolean isSameColor(final Card card, final Card otherCard) {
        return card.getColor().equals(otherCard.getColor());
    }

    private boolean isSameValue(final Card card, final Card otherCard) {
        return card.getValue().equals(otherCard.getValue());
    }
}
