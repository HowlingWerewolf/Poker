package com.game.elements;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.apache.commons.collections4.ListUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HandEvaluator {
    final List<Card> cardsToEvaluate;

    // using Donat's bitmatrix idea
    final Map<Color, Integer> colorMatrix = new HashMap<>();
    final Map<Value, Integer> valueMatrix = new TreeMap<>();

    public HandEvaluator(final List<Card> cardsToEvaluate) {
        this.cardsToEvaluate = cardsToEvaluate;

        for (final Color color : Color.values()) {
            colorMatrix.put(color, 0);
        }

        for (final Value value : Value.values()) {
            valueMatrix.put(value, 0);
        }

        for (final Card card : cardsToEvaluate) {
            colorMatrix.put(card.getColor(), colorMatrix.get(card.getColor()) + 1);
            valueMatrix.put(card.getValue(), valueMatrix.get(card.getValue()) + 1);
        }

        System.out.println(colorMatrix);
        System.out.println(valueMatrix);
    }

    /**
     * Evaluates the card ranking.
     * @return the strongest hand
     */
    public Hand evaluate() {
        final Hand flushHand = getFlushHand();
        final Hand straightHand = getStraightHand();

        // royal flush
        if (flushHand != null && flushHand.equals(straightHand)) {
            return new Hand(flushHand.getCards(), Ranking.ROYAL_FLUSH);
        }

        if (flushHand != null) {
            return flushHand;
        }

        if (straightHand != null) {
            return straightHand;
        }

        final Hand pokerHand = getPokerHand();
        if (pokerHand != null) {
            return pokerHand;
        }

        final Hand drillHand = getDrillHand();
        final Hand pairHand = getPairHand();
        if (drillHand != null && pairHand != null) {
            // merge drill and pair
            return getFullHouseHand();
        }

        if (drillHand != null) {
            return drillHand;
        }


        final Hand twoPairHand = getTwoPairHand();
        if (twoPairHand != null) {
            return twoPairHand;
        }

        if (pairHand != null) {
            return pairHand;
        }

        return new Hand(List.of(), Ranking.HIGH_CARD);
    }

    private Hand getFlushHand() {
        final Optional<Map.Entry<Color, Integer>> flushEntry = colorMatrix.entrySet()
                .stream()
                .filter(color -> color.getValue() >= 5)
                .findFirst();
        
        if (flushEntry.isPresent()) {
            final List<Card> flushCards = cardsToEvaluate.stream()
                    .filter(card -> card.getColor().equals(flushEntry.get().getKey()))
                    .sorted(Comparator.comparing(card -> card.getValue().getIndex()))
                    .collect(Collectors.toList())
                    .subList(0, 5);
            return new Hand(flushCards, Ranking.FLUSH);
        }
        return null;
    }

    private Hand getStraightHand() {
        final List<Card> sortedCards = cardsToEvaluate.stream()
                .sorted(Comparator.comparing(card -> card.getValue().getIndex()))
                .collect(Collectors.toList());
        final Optional<Card> ace = cardsToEvaluate.stream()
                .filter(card -> card.getValue().equals(Value.ACE))
                .findFirst();

        for (int i = 0; i < sortedCards.size() - 4; i++) {
            final Hand hand = getStraightHand(sortedCards.subList(i, i + 5), ace.orElse(null));
            if (hand != null) {
                return hand;
            }
        }

        return null;
    }

    private Hand getStraightHand(final List<Card> cards, final Card ace) {
        boolean isStraight = true;
        final boolean isLowestStraight = ace != null &&
                cards.get(0).getValue().equals(Value.TWO);

        if (isLowestStraight) {
            for (int i = 0; i < 3; i++) {
                isStraight = isSequence(cards.get(i), cards.get(i+1));
                if (!isStraight) {
                    break;
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                isStraight = isSequence(cards.get(i), cards.get(i+1));
                if (!isStraight) {
                    break;
                }
            }
        }

        return isStraight
                ? isLowestStraight
                    ? new Hand(List.of(ace, cards.get(0), cards.get(1), cards.get(2), cards.get(3)),
                            Ranking.STRAIGHT)
                    : new Hand(cards, Ranking.STRAIGHT)
                : null;
    }

    private boolean isSequence(final Card card1, final Card card2) {
        return card2.getValue().getIndex() - card1.getValue().getIndex() == 1;
    }

    private Hand getPokerHand() {
        return getValueMatches(4, Ranking.POKER);
    }

    private Hand getDrillHand() {
        return getValueMatches(3, Ranking.DRILL);
    }

    private Hand getPairHand() {
        return getValueMatches(2, Ranking.ONE_PAIR);
    }

    private Hand getValueMatches(int matching, Ranking ranking) {
        final Optional<Map.Entry<Value, Integer>> matchedEntry = valueMatrix.entrySet()
                .stream()
                .filter(value -> value.getValue() == matching)
                .findFirst();

        if (matchedEntry.isPresent()) {
            final List<Card> pokerCards = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(matchedEntry.get().getKey()))
                    .collect(Collectors.toList());
            return new Hand(pokerCards, ranking);
        }
        return null;
    }

    private Hand getFullHouseHand() {
        final List<Map.Entry<Value, Integer>> drill = valueMatrix.entrySet()
                .stream()
                .filter(e -> e.getValue() == 3)
                .collect(Collectors.toList());

        final List<Map.Entry<Value, Integer>> pair = valueMatrix.entrySet()
                .stream()
                .filter(e -> e.getValue() == 2)
                .collect(Collectors.toList());

        if (pair.size() == 1 && drill.size() == 1) {
            final List<Card> drillCards = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(drill.get(0).getKey()))
                    .collect(Collectors.toList());
            final List<Card> pairCards = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(pair.get(0).getKey()))
                    .collect(Collectors.toList());
            return new Hand(List.of(drillCards.get(0), drillCards.get(1),
                    drillCards.get(2), pairCards.get(0), pairCards.get(1)),
                    Ranking.FULL_HOUSE);
        }
        return null;
    }

    private Hand getTwoPairHand() {
        final List<Map.Entry<Value, Integer>> twoPairs = valueMatrix.entrySet()
                .stream()
                .filter(e -> e.getValue() == 2)
                .collect(Collectors.toList());

        if (twoPairs.size() == 2) {
            final List<Card> firstPair = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(twoPairs.get(0).getKey()))
                    .collect(Collectors.toList());
            final List<Card> secondPair = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(twoPairs.get(1).getKey()))
                    .collect(Collectors.toList());
            final Optional<Card> kicker =
                    ListUtils.subtract(ListUtils.subtract(cardsToEvaluate, firstPair), secondPair).stream()
                    .max(Comparator.comparing(card -> card.getValue().getIndex()));
            assert (kicker.isPresent());
            return new Hand(List.of(firstPair.get(0), firstPair.get(1),
                    secondPair.get(0), secondPair.get(1), kicker.get()),
                    Ranking.TWO_PAIRS);
        }
        return null;
    }
}
