package com.game.calculators;

import com.game.actor.Player;
import com.game.playground.Deck;
import com.game.playground.Table;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OutCalculatorTest implements CardDrawing {

    final boolean announcerEnabled = true;

    @Test
    void outTest() {
        // given
        final Table table = new Table(announcerEnabled);
        final Deck deck = table.getDeck();
        final Player player = new Player("playerOne");

        // find two cards from the deck
        player.getCards().add(findCardFromDeck(Color.CLUB, Value.ACE, deck));
        player.getCards().add(findCardFromDeck(Color.CLUB, Value.KING, deck));

        // flop, but we specifically draw these cards from the deck
        final List<Card> flippedCards = table.getFlippedCards();
        flippedCards.add(findCardFromDeck(Color.CLUB, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.HEART, Value.QUEEN, deck));
        flippedCards.add(findCardFromDeck(Color.SPADE, Value.TWO, deck));

        // when
        final List<List<Card>> result =
                OutCalculator.getAllOuts(player.getCards(), flippedCards, deck.getFlippedDownDeck());

        // then
        assertNotNull(result);
    }

}
