package com.game.calculators;

import com.game.actor.Player;
import com.game.playground.Deck;
import com.game.playground.Table;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OddsCalculatorTest implements CardDrawing {

    final boolean announcerEnabled = true;

    @Test
    void setWinRatioTwoPlayerFlopTest() throws IllegalAccessException {
        // given
        final Table table = new Table(announcerEnabled);
        final Deck deck = table.getDeck();

        // find two cards from the deck
        final Player playerOne = new Player();
        giveCardToPlayerFromDeck(Color.CLUB, Value.ACE, playerOne, deck);
        giveCardToPlayerFromDeck(Color.CLUB, Value.KING, playerOne, deck);

        // find two cards from the deck
        final Player playerTwo = new Player();
        giveCardToPlayerFromDeck(Color.HEART, Value.TWO, playerTwo, deck);
        giveCardToPlayerFromDeck(Color.SPADE, Value.SEVEN, playerTwo, deck);

        // flop, but we specifically draw these cards from the deck
        final List<Card> flippedCards = table.getFlippedCards();
        flippedCards.add(findCardFromDeck(Color.CLUB, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.HEART, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.SPADE, Value.THREE, deck));

        // when
        final OddsCalculator oddsCalculator = new OddsCalculator();
        final List<Player> players = List.of(playerOne, playerTwo);
        oddsCalculator.setWinRatio(players, table);

        // then
        assertPlayersWinRatioSumIsOne(players);
    }

    private void giveCardToPlayerFromDeck(final Color color, final Value value, final Player player, final Deck deck) {
        player.getCards().add(findCardFromDeck(color, value, deck));
    }

    private static void assertPlayersWinRatioSumIsOne(final List<Player> players) {
        final AtomicReference<Double> sum = new AtomicReference<>(0.0d);
        players.forEach(player -> sum.updateAndGet(v -> v + player.getWinRatio()));
        assertEquals(1.0d, sum.get());
    }

    @Test
    void setWinRatioThreePlayerFlopTest() throws IllegalAccessException {
        // given
        final Table table = new Table(announcerEnabled);
        final Deck deck = table.getDeck();

        // find two cards from the deck
        final Player playerOne = new Player();
        giveCardToPlayerFromDeck(Color.CLUB, Value.ACE, playerOne, deck);
        giveCardToPlayerFromDeck(Color.CLUB, Value.KING, playerOne, deck);

        // find two cards from the deck
        final Player playerTwo = new Player();
        giveCardToPlayerFromDeck(Color.HEART, Value.TWO, playerTwo, deck);
        giveCardToPlayerFromDeck(Color.SPADE, Value.SEVEN, playerTwo, deck);

        // find two cards from the deck
        final Player playerThree = new Player();
        giveCardToPlayerFromDeck(Color.HEART, Value.FOUR, playerThree, deck);
        giveCardToPlayerFromDeck(Color.SPADE, Value.FIVE, playerThree, deck);

        // flop, but we specifically draw these cards from the deck
        final List<Card> flippedCards = table.getFlippedCards();
        flippedCards.add(findCardFromDeck(Color.CLUB, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.HEART, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.SPADE, Value.THREE, deck));

        // when
        final OddsCalculator oddsCalculator = new OddsCalculator();
        final List<Player> players = List.of(playerOne, playerTwo, playerThree);
        oddsCalculator.setWinRatio(players, table);

        // then
        assertPlayersWinRatioSumIsOne(players);
    }

    @Test
    void setWinRatioThreePlayerPreflopTest() throws IllegalAccessException {
        // given
        final Table table = new Table(announcerEnabled);
        final Deck deck = table.getDeck();

        // find two cards from the deck
        final Player playerOne = new Player();
        giveCardToPlayerFromDeck(Color.CLUB, Value.ACE, playerOne, deck);
        giveCardToPlayerFromDeck(Color.CLUB, Value.KING, playerOne, deck);

        // find two cards from the deck
        final Player playerTwo = new Player();
        giveCardToPlayerFromDeck(Color.HEART, Value.TWO, playerTwo, deck);
        giveCardToPlayerFromDeck(Color.SPADE, Value.SEVEN, playerTwo, deck);

        // find two cards from the deck
        final Player playerThree = new Player();
        giveCardToPlayerFromDeck(Color.HEART, Value.FOUR, playerThree, deck);
        giveCardToPlayerFromDeck(Color.SPADE, Value.FIVE, playerThree, deck);

        // when
        final OddsCalculator oddsCalculator = new OddsCalculator();
        final List<Player> players = List.of(playerOne, playerTwo, playerThree);
        oddsCalculator.setWinRatio(players, table);

        // then
        assertPlayersWinRatioSumIsOne(players);
    }

}
