package com.game.calculators;

import com.game.actor.Player;
import com.game.elements.Hand;
import com.game.playground.Table;
import com.game.playground.asset.Card;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class OddsCalculator {

    /**
     * Evaluates all outs and decides for each which has the strongest hand.
     *
     * @param players
     * @param table
     */
    public void setWinRatio(final List<Player> players, final Table table) {
        for (final var player : players) {
            calculateOdds(table, player);
        }

        // possibleOutcomes are the cards that can be drawn from the deck
        final Player firstPlayer = players.stream().findFirst().orElseThrow(IllegalArgumentException::new);
        final Set<List<Card>> possibleOutcomes = firstPlayer.getOuts().keySet();
        final Map<Player, Integer> wins = new HashMap<>();
        players.forEach(player -> wins.put(player, 0));

        for (final var possibleOutcome : possibleOutcomes) {
            // this is a maximum search for each possibleOutcome
            // note that draw is possible
            final List<Player> playersWithStrongestHand = new ArrayList<>();
            Hand strongestHand = null;

            for (final var player : players) {
                strongestHand = evaluateStrongestHand(possibleOutcome, player, playersWithStrongestHand, strongestHand);
            }

            playersWithStrongestHand.forEach(player -> wins.put(player, wins.get(player) + 1));
        }

        final AtomicReference<Double> winsWithDraws = new AtomicReference<>((double) 0);
        wins.values().forEach(win -> winsWithDraws.getAndSet((winsWithDraws.get() + win)));

        for (var player : players) {
            player.setWinRatio((double) wins.get(player) / winsWithDraws.get());
        }
    }

    private Hand evaluateStrongestHand(List<Card> key, Player player, List<Player> playersWithStrongestHand, Hand strongestHand) {
        final var cardsAvailable = player.getOuts().get(key);
        if (CollectionUtils.isEmpty(cardsAvailable)) {
            throw new IllegalArgumentException();
        }

        final var possibleOutcome = new ArrayList<>(cardsAvailable);
        possibleOutcome.addAll(key);

        final Hand hand = new HandEvaluator(possibleOutcome).evaluate();

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

    private void calculateOdds(final Table table, final Player player) {
        player.getOuts().clear();
        final Map<List<Card>, List<Card>> allOuts =
                OutCalculator.getAllOuts(player.getCards(), table.getFlippedCards(), table.getDeck().getFlippedDownDeck());
        player.getOuts().putAll(allOuts);
    }

    /**
     * @param outs
     */
    public void getOddsForOnePlayer(final Map<List<Card>, List<Card>> outs, final int playerCount) {
        // TODO generate all possible hands for other players and
        // TODO calculate outcomes where the current player wins
    }

}
