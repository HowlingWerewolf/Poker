package com.game;

import com.game.actor.Player;
import com.game.calculators.HandEvaluator;
import com.game.calculators.OutCalculatorUtil;
import com.game.elements.Hand;
import com.game.playground.Board;
import com.game.playground.Deck;
import com.game.playground.asset.Card;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class TexasGameSimulation {

    final Board board;
    final Deck deck;

    final boolean openCardsModeEnabled;
    final boolean outCalculationModeEnabled;
    final boolean announcerEnabled;

    TexasGameSimulation() {
        board = new Board();
        deck = new Deck();
        openCardsModeEnabled = true;
        outCalculationModeEnabled = true;
        announcerEnabled = true;
    }

    TexasGameSimulation(final boolean openCardsModeEnabled,
                        final boolean outCalculationModeEnabled,
                        final boolean announcerEnabled) {
        board = new Board();
        deck = new Deck(announcerEnabled);
        this.openCardsModeEnabled = openCardsModeEnabled;
        this.outCalculationModeEnabled = outCalculationModeEnabled;
        this.announcerEnabled = announcerEnabled;
    }

    public void play() {

        // deal
        if (announcerEnabled) {
            log.info("Dealing...");
        }

        final Player me = new Player();
        dealToPlayer(me);

        final Player miniMe = new Player();
        dealToPlayer(miniMe);

        if (openCardsModeEnabled) {
            // we can watch the players' cards
            revealCards(me);
            revealCards(miniMe);
        }

        if (outCalculationModeEnabled) {
            // some info for Gyuri Korda
            calculateOuts(me);
            calculateOuts(miniMe);
        }

        burn();

        // flop
        if (announcerEnabled) {
            log.info("Flop!");
        }

        board.getFlippedCards().add(deck.drawFromDeck());
        board.getFlippedCards().add(deck.drawFromDeck());
        board.getFlippedCards().add(deck.drawFromDeck());

        if (announcerEnabled) {
            log.info(board.getFlippedCards().toString());
        }

        if (outCalculationModeEnabled) {
            // some info for Gyuri Korda
            calculateOuts(me);
            calculateOuts(miniMe);
        }

        burn();

        // turn
        if (announcerEnabled) {
            log.info("Turn!");
        }

        final Card turnCard = deck.drawFromDeck();
        board.getFlippedCards().add(turnCard);

        if (announcerEnabled) {
            log.info(turnCard.toString());
        }

        if (outCalculationModeEnabled) {
            // some info for Gyuri Korda
            calculateOuts(me);
            calculateOuts(miniMe);
        }

        burn();

        // river
        if (announcerEnabled) {
            log.info("River!");
        }

        final Card riverCard = deck.drawFromDeck();
        board.getFlippedCards().add(riverCard);

        if (announcerEnabled) {
            log.info(riverCard.toString());
        }


        if (announcerEnabled) {
            log.info("Me:");
        }
        final List<Card> myCards = new ArrayList<>(board.getFlippedCards());
        myCards.addAll(me.getCards());
        final HandEvaluator myHandEvaluator = new HandEvaluator(myCards);
        final Hand myHand = myHandEvaluator.evaluate();

        if (announcerEnabled) {
            log.info("My hand is: " + myHand.getCards().toString());
            log.info(myHand.getRanking().toString());
            log.info("My strongest combination is: " + myHand.getStrongestCombination().toString());
        }

        if (announcerEnabled) {
            log.info("MiniMe:");
        }

        final List<Card> otherCards = new ArrayList<>(board.getFlippedCards());
        otherCards.addAll(miniMe.getCards());
        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherCards);
        final Hand otherHand = otherHandEvaluator.evaluate();

        if (announcerEnabled) {
            log.info("MiniMe's hand is: " + otherHand.getCards().toString());
            log.info(otherHand.getRanking().toString());
            log.info("MiniMe's strongest combination is: " + otherHand.getStrongestCombination().toString());
        }

        if (announcerEnabled) {
            if (myHand.compareTo(otherHand) > 0) {
                log.info("I won");
            } else if (myHand.compareTo(otherHand) == 0) {
                log.info("It's a draw");
            } else {
                log.info("Minime won");
            }
        }
    }

    private static void revealCards(final Player player) {
        log.info(player.getCards().toString());
    }

    private void dealToPlayer(final Player player) {
        final List<Card> cards = player.getCards();
        cards.add(deck.drawFromDeck());
        cards.add(deck.drawFromDeck());
        Collections.sort(cards);
    }

    /**
     * Burns a card so the deck gets more shuffled
     */
    private void burn() {
        deck.drawFromDeck();
    }

    private void calculateOuts(final Player player) {
        log.info(OutCalculatorUtil.getAllOuts(new Hand(player.getCards(), null, null),
                deck.getFlippedDownDeck()).toString());
    }

}
