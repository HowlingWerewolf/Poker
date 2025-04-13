package com.game.elements;

import com.game.calculators.HandComparatorUtil;
import com.game.calculators.HandEvaluator;
import com.game.playground.asset.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Log
public class Hand implements Comparable<Hand> {

    private List<Card> cards;
    private List<Card> strongestCombination;
    private Ranking ranking;

    public Hand(final List<Card> cards) {
        // we just got cards unevaluated - let's perform some evaluation!
        this.cards = cards;
        final HandEvaluator handEvaluator = new HandEvaluator(this.getCards());
        final Hand evaluated = handEvaluator.evaluate();
        this.strongestCombination = evaluated.getStrongestCombination();
        this.ranking = evaluated.getRanking();
    }

    @Override
    public int compareTo(final Hand otherHand) {
        final HandEvaluator handEvaluator = new HandEvaluator(this.getCards());
        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherHand.getCards());

        final Hand evaluatedHand = handEvaluator.evaluate();
        final Hand otherEvaluatedHand = otherHandEvaluator.evaluate();

        // firstly the ranking decides
        if (!evaluatedHand.ranking.getStrength().equals(otherEvaluatedHand.ranking.getStrength())) {
            return evaluatedHand.ranking.getStrength().compareTo(otherEvaluatedHand.ranking.getStrength());
        }

        // ranking is the same
        // secondly each ranking has their own rule for decision
        return switch (this.ranking) {
            case HIGH_CARD -> HandComparatorUtil.compareHighCardHands(evaluatedHand, otherEvaluatedHand);
            case ONE_PAIR -> HandComparatorUtil.comparePairHands(evaluatedHand, otherEvaluatedHand);
            case TWO_PAIRS -> HandComparatorUtil.compareTwoPairHands(evaluatedHand, otherEvaluatedHand);
            case DRILL -> HandComparatorUtil.compareDrillHands(evaluatedHand, otherEvaluatedHand);
            case STRAIGHT -> HandComparatorUtil.compareStraightHands(evaluatedHand, otherEvaluatedHand);
            case FLUSH -> HandComparatorUtil.compareFlushHands(evaluatedHand, otherEvaluatedHand);
            case FULL_HOUSE -> HandComparatorUtil.compareFullHouseHands(evaluatedHand, otherEvaluatedHand);
            case POKER -> HandComparatorUtil.comparePokerHands(evaluatedHand, otherEvaluatedHand);
            case STRAIGHT_FLUSH -> HandComparatorUtil.compareStraightFlushHands(evaluatedHand, otherEvaluatedHand);
        };
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
            if (!card.isSameColor(otherCard) || !card.isSameValue(otherCard)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards, ranking);
    }

}
