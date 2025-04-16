package com.game.calculators;

import cc.redberry.combinatorics.Combinatorics;
import com.game.actor.Player;
import com.game.elements.Hand;
import com.game.playground.Table;
import com.game.playground.asset.Card;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Log
public class OddsCalculator {

    /**
     * Evaluates all outs and decides for each which has the strongest hand.
     *
     * @param players players currently playing at the table
     * @param table   the playground
     */
    public void setWinRatio(final List<Player> players, final Table table) throws IllegalAccessException {
        calculateOuts(table, players);

        final Map<Player, Integer> wins = calculateWinsForPlayers(players, table);

        // sum up all cases where the player or players won
        final var sum = new AtomicReference<>(0);
        wins.values().forEach(win -> sum.getAndSet((sum.get() + win)));

        // set the calculated win ratio for the players
        players.forEach(player -> player.setWinRatio((double) wins.get(player) / (double) sum.get()));
    }

    private Map<Player, Integer> calculateWinsForPlayers(final List<Player> players, final Table table) {
        final Map<Player, Integer> wins = HashMap.newHashMap(players.size());
        players.forEach(player -> wins.put(player, 0));

        // possibleOutcomes are the cards that can be drawn from the deck
        for (final var possibleOutcome : table.getOuts()) {
            // this is a maximum search for each possibleOutcome
            // note that draw is possible
            final var playersWithStrongestHand = findStrongestHands(players, table, possibleOutcome);
            playersWithStrongestHand.getRight().forEach(player -> wins.put(player, wins.get(player) + 1));
        }
        return wins;
    }

    private Pair<Hand, List<Player>> findStrongestHands(final List<Player> players, final Table table,
                                                        final List<Card> possibleOutcome) {
        final List<Player> playersWithStrongestHand = new ArrayList<>(1);
        Hand strongestHand = null;

        if (CollectionUtils.isEmpty(table.getDeck().getFlippedDownDeck())) {
            throw new IllegalArgumentException();
        }

        for (final var player : players) {
            final Hand hand = evaluateHand(possibleOutcome, table, player);
            strongestHand = getStrongestHand(player, hand, playersWithStrongestHand, strongestHand);
        }
        return Pair.of(strongestHand, playersWithStrongestHand);
    }

    private Hand evaluateHand(final List<Card> possibleOutcome, final Table table, final Player player) {
        final List<Card> cardCombination = Stream.of(player.getCards(), table.getFlippedCards(), possibleOutcome)
                .flatMap(Collection::stream).toList();
        return new HandEvaluator(cardCombination).evaluate();
    }

    private Hand getStrongestHand(final Player player, final Hand hand,
                                  final List<Player> playersWithStrongestHand, Hand strongestHand) {
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
                OutCalculator.getAllOuts(players.stream().findFirst()
                                .orElseThrow(IllegalAccessException::new).getCards(), table.getFlippedCards(),
                        table.getDeck().getFlippedDownDeck());
        table.getOuts().addAll(allOuts);
    }

    /**
     * Calculates odds blindly for one player.
     *
     * @param player         the player to calculate for
     * @param table          the table where the player are
     * @param cardsToBeDrawn number of cards will be placed on the table
     */
    public void getOddsForOnePlayer(final Player player, final Table table, final int cardsToBeDrawn) {
        if (player.getCards().isEmpty()) {
            throw new IllegalArgumentException();
        }

        final var clonedTable = table.clone();
        final var flippedDownDeckCopy = new ArrayList<>(table.getDeck().getFlippedDownDeck());

        final Map<Player, Integer> wins = HashMap.newHashMap(table.getPlayers().size());
        table.getPlayers().forEach(p -> wins.put(p, 0));

        final AtomicReference<Double> counter = new AtomicReference<>(0d);
        // mini decks representing those cards that will appear in the games
        // FIXME: the order of players' cards and flipped cards separately does not matter:
        //      2 * player cards
        //      max 5 flipped cards
        //      -> I hate to say this but its going to be a recursion
        //          - cannot store this much elements in memory
        //          - separating combination generations to draw independently by player
        Combinatorics.combinations(flippedDownDeckCopy.toArray(),
                        2 * (table.getPlayers().size() - 1) + cardsToBeDrawn)
                .stream()
                .map(o -> Arrays.stream(o).map(c -> (Card) c).toList())
                .forEach(miniDeck -> {
                    final List<Card> deck = clonedTable.getDeck().getCards();
                    deck.clear();
                    deck.addAll(miniDeck);

                    for (final var virtualPlayer : clonedTable.getPlayers()) {
                        if (virtualPlayer.getUniqueName().equals(player.getUniqueName())) {
                            continue;
                        }
                        final List<Card> virtualPlayerCards = virtualPlayer.getCards();
                        virtualPlayerCards.clear();
                        virtualPlayerCards.add(deck.removeFirst());
                        virtualPlayerCards.add(deck.removeFirst());
                    }

                    if (deck.size() != cardsToBeDrawn) {
                        throw new IllegalArgumentException();
                    }

                    // register partial wins to overall table
                    calculateWinsForPlayers(clonedTable.getPlayers(), clonedTable)
                            .forEach((virtualPlayer, partialWinCount) ->
                                    wins.put(virtualPlayer, partialWinCount + wins.get(virtualPlayer)));

                    counter.getAndSet(counter.get() + 1);
                    if (counter.get() % 10000000 == 0) {
                        log.info("Reached " + counter.get());
                    }
                });

        // sum up all cases where the player or players won
        final var sum = new AtomicReference<>(0);
        wins.values().forEach(win -> sum.getAndSet((sum.get() + win)));

        // copy the win ratio to the real table
        wins.forEach((virtualPlayer, winCount) -> {
            final Player realPlayer = table.getPlayers().stream()
                    .filter(p -> p.getUniqueName().equals(virtualPlayer.getUniqueName()))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
            realPlayer.setWinRatio((double) winCount / (double) sum.get());
            log.info("Player: " + realPlayer.getUniqueName() + " - win ratio: " + realPlayer.getWinRatio());
        });
    }

}
