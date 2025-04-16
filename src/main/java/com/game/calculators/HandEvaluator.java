package com.game.calculators;

import cc.redberry.combinatorics.Combinatorics;
import com.game.elements.Hand;
import com.game.elements.Ranking;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.game.calculators.Constants.MAX_HAND_SIZE;

@Getter
public class HandEvaluator {

    final List<Card> cardsToEvaluate;

    // using Donat's bitmatrix idea
    final Map<Color, Integer> colorMatrix = new EnumMap<>(Color.class);
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
            colorMatrix.put(card.color(), colorMatrix.get(card.color()) + 1);
            valueMatrix.put(card.value(), valueMatrix.get(card.value()) + 1);
        }
    }

    /**
     * Evaluates the card ranking.
     *
     * @return the strongest hand
     */
    public Hand evaluate() {
        final List<Hand> flushHands = getFlushHands();
        final List<Hand> straightHands = getStraightHands();

        // royal flush
        final List<List<Card>> straightFlushHands = CollectionUtils.intersection(
                        new HashSet<>(flushHands.stream().map(Hand::getStrongestCombination).toList()),
                        new HashSet<>(straightHands.stream().map(Hand::getStrongestCombination).toList()))
                .stream().toList();
        if (CollectionUtils.isNotEmpty(straightFlushHands)) {
            return getStraightFlushHand(flushHands, straightFlushHands);
        }

        if (CollectionUtils.isNotEmpty(flushHands)) {
            // ordered by value, so the first is always the strongest
            return flushHands.getFirst();
        }

        if (CollectionUtils.isNotEmpty(straightHands)) {
            return getHighestStraight(straightHands);
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

    private Hand getStraightFlushHand(final List<Hand> flushHands, final List<List<Card>> straightFlushHands) {
        final List<Hand> hands = flushHands.stream()
                .filter(hand -> straightFlushHands.contains(hand.getStrongestCombination()))
                .toList();
        final Hand highestStraightFlush = getHighestStraight(hands);
        highestStraightFlush.setRanking(Ranking.STRAIGHT_FLUSH);
        return highestStraightFlush;
    }

    private Hand getHighestStraight(final List<Hand> straightHands) {
        if (CollectionUtils.isEmpty(straightHands)) {
            throw new IllegalArgumentException();
        }

        // initially the lowest possible straight, can we find better?
        Value maxValue = Value.FIVE;
        Hand highestStraightHand = straightHands.getFirst();
        for (final Hand hand : straightHands) {
            final List<Card> straightCards = hand.getStrongestCombination();
            HandComparatorUtil.sortCardsDescending(straightCards);
            if (highestStraightHand == null ||
                    straightCards.getFirst().value().getIndex() > maxValue.getIndex()) {
                highestStraightHand = hand;
                maxValue = straightCards.getFirst().value();
            }
        }
        return highestStraightHand;
    }

    private List<Hand> getFlushHands() {
        final Optional<Map.Entry<Color, Integer>> flushEntry = colorMatrix.entrySet()
                .stream()
                .filter(color -> color.getValue() >= 5)
                .findFirst();

        if (flushEntry.isPresent()) {
            final List<Card> flushCards = cardsToEvaluate.stream()
                    .filter(card -> card.color().equals(flushEntry.get().getKey()))
                    .sorted(Comparator.comparing(card -> card.value().getIndex()))
                    .sorted(Comparator.reverseOrder())
                    .toList();

            if (flushCards.size() == 5) {
                return List.of(new Hand(cardsToEvaluate, flushCards, Ranking.FLUSH));
            }

            return Combinatorics.combinations(flushCards.toArray(), 5)
                    .stream()
                    .map(o -> Arrays.stream(o).map(c -> (Card) c).toList())
                    .map(cards -> new Hand(cardsToEvaluate, cards, Ranking.FLUSH))
                    .toList();
        }
        return List.of();
    }

    private List<Hand> getStraightHands() {
        final int distinctValueCount = getDistinctValues().size();
        if (distinctValueCount < 5) {
            return List.of();
        }

        // partition valuematrix by neighbors - any with size 5 indicates we have a straight
        final boolean hasStraight = getPartitions().stream().anyMatch(partition -> partition.size() >= 5);

        if (hasStraight) {
            final List<Card> sortedCardsDescending = HandComparatorUtil.sortCardsDescending(cardsToEvaluate);
            final List<List<Card>> combinations =
                    Combinatorics.combinations(sortedCardsDescending.toArray(), 5)
                            .stream()
                            .map(o -> Arrays.stream(o).map(c -> (Card) c).toList())
                            .toList();

            final List<Hand> hands = new ArrayList<>();
            for (final List<Card> fiveCards : combinations) {
                final Hand hand = getStraightHands(fiveCards);
                if (hand != null) {
                    hands.add(hand);
                }
            }
            return hands;
        }

        final Optional<Hand> lowestStraightOrEmpty = getLowestStraightOrEmpty();
        return lowestStraightOrEmpty.map(List::of).orElseGet(List::of);
    }

    private List<List<Value>> getPartitions() {
        final List<Value> values = valueMatrix.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .sorted((o1, o2) -> o2.getIndex().compareTo(o1.getIndex()))
                .toList();

        final List<List<Value>> partitionedValues = new ArrayList<>(7);
        List<Value> currentPartition = new ArrayList<>();
        Value previousValue = null;
        for (final Value value : values) {
            if (previousValue == null) {
                currentPartition.add(value);
            } else if (previousValue.getIndex() - 1 == value.getIndex()) {
                currentPartition.add(value);
            } else {
                partitionedValues.add(currentPartition.stream().toList());
                currentPartition.clear();
                currentPartition.add(value);
            }
            previousValue = value;
        }

        if (CollectionUtils.isNotEmpty(currentPartition)) {
            partitionedValues.add(currentPartition.stream().toList());
            currentPartition.clear();
        }
        return partitionedValues;
    }

    private Optional<Hand> getLowestStraightOrEmpty() {
        // check if the ace considered as one then the straight
        // by removing elements from a complete lowest straight
        final Set<Value> theLowestStraight =
                SetUtils.hashSet(Value.ACE, Value.TWO, Value.THREE, Value.FOUR, Value.FIVE);
        valueMatrix.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .forEach(theLowestStraight::remove);

        if (theLowestStraight.isEmpty()) {
            final List<Card> cards = cardsToEvaluate.stream()
                    .filter(card -> SetUtils.hashSet(Value.ACE, Value.TWO, Value.THREE, Value.FOUR, Value.FIVE).contains(card.value()))
                    .toList();
            final List<Card> sortedCards = HandComparatorUtil.sortCardsDescending(cards);

            keepOnlyDominantColor(sortedCards);

            return Optional.of(new Hand(cardsToEvaluate, List.of(sortedCards.get(4), sortedCards.get(0), sortedCards.get(1),
                    sortedCards.get(2), sortedCards.get(3)), Ranking.STRAIGHT));
        }
        return Optional.empty();
    }

    private void keepOnlyDominantColor(final List<Card> sortedCards) {
        if (sortedCards.size() > 5) {
            // if there's more cards for the lowest straight, a pair, only keep the one with dominant color
            final Map<Color, Long> colorCount = sortedCards.stream()
                    .map(Card::color)
                    .collect(Collectors.groupingBy(color -> color, Collectors.counting()));
            final AtomicReference<Long> max = new AtomicReference<>((long) 0);
            colorCount.values().forEach(count -> {
                if (count > max.get()) {
                    max.getAndSet(count);
                }
            });
            final Color dominantColor = colorCount.entrySet().stream()
                    .filter(e -> e.getValue().equals(max.get()))
                    .map(Map.Entry::getKey)
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            for (int i = sortedCards.size(); i > 5; i--) {
                final Optional<Card> removable = sortedCards.stream()
                        .filter(card -> valueMatrix.get(card.value()) > 1)
                        .filter(card -> card.color().equals(dominantColor)).findFirst();
                if (removable.isPresent()) {
                    sortedCards.remove(removable.get());
                } else {
                    final Optional<Card> colorIndependentRemovable = sortedCards.stream()
                            .filter(card -> valueMatrix.get(card.value()) > 1).findFirst();
                    if (colorIndependentRemovable.isPresent()) {
                        sortedCards.remove(colorIndependentRemovable.get());
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        if (sortedCards.size() != 5) {
            throw new IllegalArgumentException();
        }
    }

    private List<Value> getDistinctValues() {
        return valueMatrix.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    private Hand getStraightHands(final List<Card> cards) {
        final Set<Value> values = cards.stream().map(Card::value).collect(Collectors.toUnmodifiableSet());
        if (values.size() != 5) {
            return null;
        }

        if (isIsStraight(cards)) {
            return new Hand(cardsToEvaluate, cards, Ranking.STRAIGHT);
        }

        return null;
    }

    private boolean isIsStraight(final List<Card> cards) {
        boolean optimisticCheck = true;
        for (int i = cards.size() - 1; i > 0; i--) {
            optimisticCheck = isSequence(cards.get(i), cards.get(i - 1));
            if (!optimisticCheck) {
                break;
            }
        }
        return optimisticCheck;
    }

    private boolean isSequence(final Card card1, final Card card2) {
        return card2.value().getIndex() - card1.value().getIndex() == 1;
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
                .max(Map.Entry.comparingByKey());

        if (matchedEntry.isPresent()) {
            final List<Card> matchedCards = cardsToEvaluate.stream()
                    .filter(card -> card.value().equals(matchedEntry.get().getKey()))
                    .toList();
            final List<Card> kickers = HandComparatorUtil.sortCardsDescending(ListUtils.subtract(cardsToEvaluate, matchedCards))
                    .subList(0, MAX_HAND_SIZE - matching);

            return new Hand(cardsToEvaluate, HandComparatorUtil.sortCardsDescending(ListUtils.union(matchedCards, kickers)), ranking);
        }
        return null;
    }

    private Hand getFullHouseHand() {
        final Optional<Map.Entry<Value, Integer>> drill = valueMatrix.entrySet()
                .stream()
                .filter(e -> e.getValue() == 3)
                .max(Map.Entry.comparingByKey());

        final Optional<Map.Entry<Value, Integer>> pair = valueMatrix.entrySet()
                .stream()
                .filter(e -> e.getValue() == 2)
                .max(Map.Entry.comparingByKey());

        if (pair.isPresent() && drill.isPresent()) {
            final List<Card> drillCards = cardsToEvaluate.stream()
                    .filter(card -> card.value().equals(drill.get().getKey()))
                    .toList();
            final List<Card> pairCards = cardsToEvaluate.stream()
                    .filter(card -> card.value().equals(pair.get().getKey()))
                    .toList();
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
                .toList();

        if (twoPairs.size() == 2) {
            final List<Card> firstPair = cardsToEvaluate.stream()
                    .filter(card -> card.value().equals(twoPairs.getFirst().getKey()))
                    .toList();
            final List<Card> secondPair = cardsToEvaluate.stream()
                    .filter(card -> card.value().equals(twoPairs.get(1).getKey()))
                    .toList();
            final Optional<Card> kicker =
                    ListUtils.subtract(ListUtils.subtract(cardsToEvaluate, firstPair), secondPair).stream()
                            .max(Comparator.comparing(card -> card.value().getIndex()));
            assert (kicker.isPresent());
            return new Hand(cardsToEvaluate,
                    List.of(firstPair.get(0), firstPair.get(1), secondPair.get(0), secondPair.get(1), kicker.get()),
                    Ranking.TWO_PAIRS);
        }
        return null;
    }

}
