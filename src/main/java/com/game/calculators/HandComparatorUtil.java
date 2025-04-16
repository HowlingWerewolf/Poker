package com.game.calculators;

import com.game.elements.Hand;
import com.game.playground.asset.Card;
import com.game.playground.asset.Value;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.game.calculators.Constants.DRILL_SIZE;
import static com.game.calculators.Constants.MAX_HAND_SIZE;
import static com.game.calculators.Constants.PAIR_SIZE;
import static com.game.calculators.Constants.POKER_SIZE;

public class HandComparatorUtil {

    private HandComparatorUtil() {
    }

    public static int compareHighCardHands(final Hand hand, final Hand otherHand) {
        final List<Card> sortedCards = sortCardsDescending(hand);
        final List<Card> sortedOtherCards = sortCardsDescending(otherHand);
        return compareOneByOne(sortedCards, sortedOtherCards);
    }

    private static Integer compareOneByOne(final List<Card> cards, final List<Card> otherCards) {
        final List<Card> sortedCards = sortCardsDescending(cards);
        final List<Card> sortedOtherCards = sortCardsDescending(otherCards);

        for (int i = 0; i < sortedCards.size(); i++) {
            final Card card = sortedCards.get(i);
            final Card otherCard = sortedOtherCards.get(i);

            final int compared = card.compareTo(otherCard);
            if (compared != 0) {
                return compared;
            }
        }
        return 0;
    }

    private static List<Card> sortCardsDescending(final Hand hand) {
        return sortCardsDescending(hand.getCards());
    }

    protected static List<Card> sortCardsDescending(final List<Card> cards) {
        return cards.stream()
                .sorted(Comparator.comparing(Card::value))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static int comparePairHands(final Hand hand, final Hand otherHand) {
        final HandEvaluator handEvaluator = new HandEvaluator(hand.getStrongestCombination());
        final Optional<Map.Entry<Value, Integer>> pairs = handEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(value -> value.getValue() == PAIR_SIZE)
                .findFirst();

        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherHand.getStrongestCombination());
        final Optional<Map.Entry<Value, Integer>> otherPairs = otherHandEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(value -> value.getValue() == PAIR_SIZE)
                .findFirst();

        assert (pairs.isPresent() && otherPairs.isPresent());
        if (pairs.get().getKey() != otherPairs.get().getKey()) {
            final Value firstPairStrength = Value.valueOf(pairs.get().getKey().toString());
            final Value otherFirstPairStrength = Value.valueOf(otherPairs.get().getKey().toString());
            return firstPairStrength.compareTo(otherFirstPairStrength);
        } else {
            // compare kickers
            final List<Card> kickers = hand.getCards().stream()
                    .filter(card -> !card.value().equals(pairs.get().getKey()))
                    .toList();
            final List<Card> otherKickers = otherHand.getCards().stream()
                    .filter(card -> !card.value().equals(pairs.get().getKey()))
                    .toList();

            return compareOneByOne(kickers, otherKickers);
        }
    }

    public static int compareTwoPairHands(final Hand hand, final Hand otherHand) {
        final HandEvaluator handEvaluator = new HandEvaluator(hand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> twoPairs = handEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == PAIR_SIZE)
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .toList();

        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherHand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> otherTwoPairs = otherHandEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == PAIR_SIZE)
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .toList();

        // compare pairs
        for (int i = 0; i < twoPairs.size(); i++) {
            final Value firstPairStrength = Value.valueOf(twoPairs.get(i).getKey().toString());
            final Value otherFirstPairStrength = Value.valueOf(otherTwoPairs.get(i).getKey().toString());
            final int compared = firstPairStrength.compareTo(otherFirstPairStrength);
            if (compared != 0) {
                return compared;
            }
        }

        // sanity check
        if (handEvaluator.getTwoPairHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (otherHandEvaluator.getTwoPairHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        // compare kickers
        final List<Card> kickers = handEvaluator.getTwoPairHand().get().getCards().stream()
                .filter(card -> !card.value().equals(twoPairs.getFirst().getKey()) && !card.value().equals(twoPairs.get(1).getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();
        final List<Card> otherKickers = otherHandEvaluator.getTwoPairHand().get().getCards().stream()
                .filter(card -> !card.value().equals(twoPairs.getFirst().getKey()) && !card.value().equals(twoPairs.get(1).getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();

        for (int i = 0; i < MAX_HAND_SIZE - (PAIR_SIZE * 2); i++) {
            final Card kicker = kickers.get(i);
            final Card otherKicker = otherKickers.get(i);
            if (!kicker.isSameValue(otherKicker)) {
                return kicker.compareTo(otherKicker);
            }
        }

        return 0;
    }

    public static int compareDrillHands(final Hand hand, final Hand otherHand) {
        final HandEvaluator handEvaluator = new HandEvaluator(hand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> drill = handEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == DRILL_SIZE)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherHand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> otherDrill = otherHandEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == DRILL_SIZE)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        // compare drills
        for (int i = 0; i < drill.size(); i++) {
            final Value firstDrillStrength = Value.valueOf(drill.get(i).getKey().toString());
            final Value otherDrillStrength = Value.valueOf(otherDrill.get(i).getKey().toString());
            final int compared = firstDrillStrength.compareTo(otherDrillStrength);
            if (compared != 0) {
                return compared;
            }
        }

        // sanity check
        if (handEvaluator.getDrillHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (otherHandEvaluator.getDrillHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        // compare kickers
        final List<Card> kickers = handEvaluator.getDrillHand().get().getCards().stream()
                .filter(card -> !card.value().equals(drill.getFirst().getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();
        final List<Card> otherKickers = otherHandEvaluator.getDrillHand().get().getCards().stream()
                .filter(card -> !card.value().equals(drill.getFirst().getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();

        for (int i = 0; i < MAX_HAND_SIZE - DRILL_SIZE; i++) {
            final Card kicker = kickers.get(i);
            final Card otherKicker = otherKickers.get(i);
            if (!kicker.isSameValue(otherKicker)) {
                return kicker.compareTo(otherKicker);
            }
        }

        return 0;
    }

    public static int compareStraightHands(final Hand hand, final Hand otherHand) {
        final Card firstCardOfStraight = hand.getStrongestCombination().getFirst();
        final Card firstCardOfOtherStraight = otherHand.getStrongestCombination().getFirst();
        return firstCardOfStraight.compareTo(firstCardOfOtherStraight);
    }

    public static int compareFlushHands(final Hand hand, final Hand otherHand) {
        return compareOneByOne(hand.getStrongestCombination(), otherHand.getStrongestCombination());
    }

    public static int compareFullHouseHands(final Hand hand, final Hand otherHand) {
        return compareDrillHands(hand, otherHand);
    }

    public static int comparePokerHands(final Hand hand, final Hand otherHand) {
        final HandEvaluator handEvaluator = new HandEvaluator(hand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> poker = handEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == POKER_SIZE)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherHand.getStrongestCombination());
        final List<Map.Entry<Value, Integer>> otherPoker = otherHandEvaluator.getValueMatrix().entrySet()
                .stream()
                .filter(e -> e.getValue() == POKER_SIZE)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        // compare drills
        for (int i = 0; i < poker.size(); i++) {
            final Value firstPokerStrength = Value.valueOf(poker.get(i).getKey().toString());
            final Value otherPokerStrength = Value.valueOf(otherPoker.get(i).getKey().toString());
            final int compared = firstPokerStrength.compareTo(otherPokerStrength);
            if (compared != 0) {
                return compared;
            }
        }

        // sanity check
        if (handEvaluator.getPokerHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (otherHandEvaluator.getPokerHand().isEmpty()) {
            throw new IllegalArgumentException();
        }

        // compare kickers
        final List<Card> kickers = handEvaluator.getPokerHand().get().getCards().stream()
                .filter(card -> !card.value().equals(poker.getFirst().getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();
        final List<Card> otherKickers = otherHandEvaluator.getPokerHand().get().getCards().stream()
                .filter(card -> !card.value().equals(poker.getFirst().getKey()))
                .sorted((c1, c2) -> c2.value().compareTo(c1.value()))
                .toList();

        for (int i = 0; i < MAX_HAND_SIZE - POKER_SIZE; i++) {
            final Card kicker = kickers.get(i);
            final Card otherKicker = otherKickers.get(i);
            if (!kicker.isSameValue(otherKicker)) {
                return kicker.compareTo(otherKicker);
            }
        }

        return 0;
    }

    public static int compareStraightFlushHands(final Hand hand, final Hand otherHand) {
        // only checks the highest card of the straight
        return compareStraightHands(hand, otherHand);
    }
}
