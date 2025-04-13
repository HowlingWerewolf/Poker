package com.game.calculators;

import com.game.playground.asset.Card;
import org.paukov.combinatorics3.Generator;

import java.util.List;

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
     * @return all possible outs
     */
    public static List<List<Card>> getAllOuts(final List<Card> cardsInHand,
                                              final List<Card> cardsFlipped,
                                              final List<Card> cardsInDeck) {
        final int remainingEmptyPositions = MAX_DEALT_CARDS - cardsInHand.size() - cardsFlipped.size();
        return Generator.combination(cardsInDeck)
                .simple(remainingEmptyPositions)
                .stream()
                .toList();
    }

}
