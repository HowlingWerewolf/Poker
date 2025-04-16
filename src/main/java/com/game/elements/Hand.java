package com.game.elements;

import com.game.calculators.HandComparatorUtil;
import com.game.calculators.HandEvaluator;
import com.game.playground.asset.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Log
public class Hand implements Comparable<Hand> {

    private List<Card> cards;
    private List<Card> strongestCombination;
    private Ranking ranking;

    /**
     * After the last card placed on the table (river at texas holdem), we can evaluate cards.
     *
     * @param cards the cards of the player and the cards placed on the table
     */
    public Hand(final List<Card> cards) {
        // we just got cards unevaluated - let's perform some evaluation!
        this.cards = cards;
        final HandEvaluator handEvaluator = new HandEvaluator(this.getCards());
        final Hand evaluated = handEvaluator.evaluate();
        this.strongestCombination = evaluated.getStrongestCombination();
        this.ranking = evaluated.getRanking();
    }

    @Override
    public int compareTo(final Hand other) {
        final Hand evaluatedHand = this.strongestCombination == null
                ? new HandEvaluator(this.getCards()).evaluate() : this;
        final Hand otherEvaluatedHand = other.getStrongestCombination() == null
                ? new HandEvaluator(other.getCards()).evaluate() : other;

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
        if (this.strongestCombination.size() != otherHand.strongestCombination.size()) {
            return false;
        }

        for (int i = 0; i < 5; i++) {
            final Card card = this.strongestCombination.get(i);
            final Card otherCard = otherHand.getStrongestCombination().get(i);
            if (card.compareTo(otherCard) == 0) {
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
