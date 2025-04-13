package com.game.calculators;

import com.game.actor.Player;
import com.game.elements.Hand;
import com.game.playground.Table;
import com.game.playground.asset.Card;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class OddsCalculator {

    /**
     * Evaluates all outs and decides for each which has the strongest hand.
     *
     * @param players players currently playing at the table
     * @param table   the playground
     */
    public void setWinRatio(final List<Player> players, final Table table) throws IllegalAccessException {
        calculateOuts(table, players);

        final Map<Player, Integer> wins = new HashMap<>();
        players.forEach(player -> wins.put(player, 0));

        // possibleOutcomes are the cards that can be drawn from the deck
        for (final var possibleOutcome : table.getOuts()) {
            // this is a maximum search for each possibleOutcome
            // note that draw is possible
            final List<Player> playersWithStrongestHand = new ArrayList<>();
            Hand strongestHand = null;

            for (final var player : players) {
                strongestHand = evaluateStrongestHand(possibleOutcome, table, player, playersWithStrongestHand, strongestHand);
            }

            playersWithStrongestHand.forEach(player -> wins.put(player, wins.get(player) + 1));
        }

        // sum up all cases where the player or players won
        final AtomicReference<Double> winsWithDraws = new AtomicReference<>((double) 0);
        wins.values().forEach(win -> winsWithDraws.getAndSet((winsWithDraws.get() + win)));

        // set the calculated win ratio for the players
        players.forEach(player -> player.setWinRatio((double) wins.get(player) / winsWithDraws.get()));
    }

    private Hand evaluateStrongestHand(final List<Card> possibleOutcome, final Table table, final Player player,
                                       final List<Player> playersWithStrongestHand, Hand strongestHand) {
        if (CollectionUtils.isEmpty(table.getDeck().getFlippedDownDeck())) {
            throw new IllegalArgumentException();
        }

        final List<Card> cardCombination = Stream.of(player.getCards(), table.getFlippedCards(), possibleOutcome)
                .flatMap(Collection::stream).toList();

        final Hand hand = new HandEvaluator(cardCombination).evaluate();

        if (playersWithStrongestHand.isEmpty()) {
            playersWithStrongestHand.add(player);
            strongestHand = hand;
        } else {
            final int compared = hand.compareTo(strongestHand);
            if (compared > 0) {
                playersWithStrongestHand.clear();
                playersWithStrongestHand.add(player);
                strongestHand = hand;
            } else if (compared == 0) {
                playersWithStrongestHand.add(player);
            }
        }
        return strongestHand;
    }

    private void calculateOuts(final Table table, final List<Player> players) throws IllegalAccessException {
        table.getOuts().clear();
        final List<List<Card>> allOuts =
                OutCalculator.getAllOuts(players.stream().findFirst().orElseThrow(IllegalAccessException::new).getCards(), table.getFlippedCards(), table.getDeck().getFlippedDownDeck());
        table.getOuts().addAll(allOuts);
    }

    /**
     * @param outs
     */
    public void getOddsForOnePlayer(final Map<List<Card>, List<Card>> outs, final int playerCount) {
        // TODO generate all possible hands for other players and
        // TODO calculate outcomes where the current player wins
    }

}
