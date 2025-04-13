package com.game.calculators;

import com.game.playground.asset.Card;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.game.calculators.Constants.MAX_DEALT_CARDS;

/**
 * Calculates outs between two hands.
 */
public class OutCalculator {

    private OutCalculator() {
    }

    /**
     * Generates all possible outs by the remaining cards.
     *
     * @param cardsInHand  the player's cards
     * @param cardsFlipped cards revealed so far
     * @param cardsInDeck  cards not revealed
     * @return Map of all possible outs for the given hand&flipped card setup, indexed by the possible card
     */
    public static Map<List<Card>, List<Card>> getAllOuts(final List<Card> cardsInHand,
                                                         final List<Card> cardsFlipped,
                                                         final List<Card> cardsInDeck) {
        final int remainingEmptyPositions = MAX_DEALT_CARDS - cardsInHand.size() - cardsFlipped.size();
        final Set<List<Card>> outs = Generator.combination(cardsInDeck)
                .simple(remainingEmptyPositions)
                .stream()
                .collect(Collectors.toSet());

        final Map<List<Card>, List<Card>> allPossibleOutcomes = new HashMap<>();
        for (final List<Card> out : outs) {
            final List<Card> setup = new ArrayList<>(cardsInHand);
            setup.addAll(cardsFlipped);
            allPossibleOutcomes.put(out, setup);
        }

        return allPossibleOutcomes;
    }

}
