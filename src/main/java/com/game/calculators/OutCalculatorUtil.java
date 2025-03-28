package com.game.calculators;

import com.game.elements.Hand;
import com.game.playground.asset.Card;
import org.paukov.combinatorics3.Generator;

import java.util.List;
import java.util.stream.Collectors;

import static com.game.calculators.Constants.MAX_DEALT_CARDS;

/**
 * Calculates outs between two hands.
 */
public class OutCalculatorUtil {

    private OutCalculatorUtil() {}

    public static List<List<Card>> getAllOuts(final Hand hand, final List<Card> remainingCards) {
        final int remainingEmptyPositions = MAX_DEALT_CARDS - hand.getCards().size();
        return Generator.combination(remainingCards)
                .simple(remainingEmptyPositions)
                .stream()
                .toList();
//        final List<Object[]> outsAsArray = Combinatorics.combinations(remainingCards.toArray(),
//                        remainingEmptyPositions)
//                .stream()
//                .toList();
//
//        final List<List<Card>> result = new ArrayList<>();
//        // TODO check repetition, the order does not matter!
//        outsAsArray.forEach(out -> {
//            final List<Card> cards = new ArrayList<>();
//            for (Object o : out) {
//                cards.add((Card) o);
//            }
//            result.add(cards);
//        });
//
//        return result;
    }

    // TODO compare hands with possible outs

}
