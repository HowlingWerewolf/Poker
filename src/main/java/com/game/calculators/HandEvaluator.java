package com.game.calculators;

import cc.redberry.combinatorics.Combinatorics;
import com.game.elements.Hand;
import com.game.elements.Ranking;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.game.calculators.Constants.MAX_HAND_SIZE;

@Getter
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
            return new Hand(cardsToEvaluate, flushHand.getCards(), Ranking.STRAIGHT_FLUSH);
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

        return new Hand(cardsToEvaluate, HandComparatorUtil.sortCardsDescending(cardsToEvaluate).subList(0, 5), Ranking.HIGH_CARD);
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
            return new Hand(cardsToEvaluate, flushCards, Ranking.FLUSH);
        }
        return null;
    }

    private Hand getStraightHand() {
        final List<Card> sortedCards = HandComparatorUtil.sortCardsDescending(cardsToEvaluate);
        final Optional<Card> ace = cardsToEvaluate.stream()
                .filter(card -> card.getValue().equals(Value.ACE))
                .findFirst();
        final List<Object[]> collect = Combinatorics.combinations(sortedCards.toArray(), 5)
                .stream()
                .collect(Collectors.toList());

        for (Object[] fiveCardHandAsObj : collect) {
            final List<Card> fiveCardHand = Arrays.stream(fiveCardHandAsObj).map(o -> (Card) o).collect(Collectors.toList());
            final Hand hand = getStraightHand(fiveCardHand, ace.orElse(null));
            if (hand != null) {
                return hand;
            }
        }

        return null;
    }

    private Hand getStraightHand(final List<Card> cards, final Card ace) {
        boolean isStraight = true;
        final boolean isLowestStraight = ace != null &&
                cards.get(cards.size() - 1).getValue().equals(Value.TWO);

        if (isLowestStraight) {
            for (int i = cards.size() - 1; i > 1; i--) {
                isStraight = isSequence(cards.get(i), cards.get(i - 1));
                if (!isStraight) {
                    break;
                }
            }
        } else {
            for (int i = cards.size() - 1; i > 0; i--) {
                isStraight = isSequence(cards.get(i), cards.get(i - 1));
                if (!isStraight) {
                    break;
                }
            }
        }

        return isStraight
                ? isLowestStraight
                    ? new Hand(cardsToEvaluate,
                            List.of(ace, cards.get(0), cards.get(1), cards.get(2), cards.get(3)),
                            Ranking.STRAIGHT)
                    : new Hand(cardsToEvaluate, cards, Ranking.STRAIGHT)
                : null;
    }

    private boolean isSequence(final Card card1, final Card card2) {
        return card2.getValue().getIndex() - card1.getValue().getIndex() == 1;
    }

    protected Hand getPokerHand() {
        return getValueMatches(4, Ranking.POKER);
    }

    protected Hand getDrillHand() {
        return getValueMatches(3, Ranking.DRILL);
    }

    private Hand getPairHand() {
        return getValueMatches(2, Ranking.ONE_PAIR);
    }

    public Hand getValueMatches(final int matching, final Ranking ranking) {
        final Optional<Map.Entry<Value, Integer>> matchedEntry = valueMatrix.entrySet()
                .stream()
                .filter(value -> value.getValue() == matching)
                .findFirst();

        if (matchedEntry.isPresent()) {
            final List<Card> matchedCards = cardsToEvaluate.stream()
                    .filter(card -> card.getValue().equals(matchedEntry.get().getKey()))
                    .collect(Collectors.toList());
            final List<Card> kickers = HandComparatorUtil.sortCardsDescending(ListUtils.subtract(cardsToEvaluate, matchedCards))
                    .subList(MAX_HAND_SIZE - matching - 1, cardsToEvaluate.size() - matchedCards.size());

            return new Hand(cardsToEvaluate, ListUtils.union(matchedCards, HandComparatorUtil.sortCardsDescending(kickers)), ranking);
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
            return new Hand(cardsToEvaluate,
                    List.of(drillCards.get(0), drillCards.get(1), drillCards.get(2), pairCards.get(0), pairCards.get(1)),
                    Ranking.FULL_HOUSE);
        }
        return null;
    }

    protected Hand getTwoPairHand() {
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
            return new Hand(cardsToEvaluate,
                    List.of(firstPair.get(0), firstPair.get(1), secondPair.get(0), secondPair.get(1), kicker.get()),
                    Ranking.TWO_PAIRS);
        }
        return null;
    }

}
