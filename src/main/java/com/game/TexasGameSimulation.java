package com.game;

import com.game.actor.Player;
import com.game.calculators.HandEvaluator;
import com.game.calculators.OutCalculator;
import com.game.elements.Hand;
import com.game.playground.Table;
import com.game.playground.Deck;
import com.game.playground.asset.Card;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class TexasGameSimulation {

    final Table table;
    final Deck deck;

    final boolean openCardsModeEnabled;
    final boolean outCalculationModeEnabled;
    final boolean announcerEnabled;

    TexasGameSimulation() {
        table = new Table();
        deck = new Deck();
        openCardsModeEnabled = true;
        outCalculationModeEnabled = false;
        announcerEnabled = true;
    }

    TexasGameSimulation(final boolean openCardsModeEnabled,
                        final boolean outCalculationModeEnabled,
                        final boolean announcerEnabled) {
        table = new Table();
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

        final List<Card> flopCards = new ArrayList<>();
        flopCards.add(flipCard(deck));
        flopCards.add(flipCard(deck));
        flopCards.add(flipCard(deck));

        if (announcerEnabled) {
            log.info(flopCards.toString());
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

        final Card turnCard = flipCard(deck);

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

        final Card riverCard = flipCard(deck);

        if (announcerEnabled) {
            log.info(riverCard.toString());
        }

        // My hand
        if (announcerEnabled) {
            log.info("Me:");
        }
        final List<Card> myCards = new ArrayList<>(table.getFlippedCards());
        myCards.addAll(me.getCards());
        final HandEvaluator myHandEvaluator = new HandEvaluator(myCards);
        final Hand myHand = myHandEvaluator.evaluate();

        if (announcerEnabled) {
            log.info("My hand is: " + myHand.getCards().toString());
            log.info(myHand.getRanking().toString());
            log.info("My strongest combination is: " + myHand.getStrongestCombination().toString());
        }

        // MiniMe's hand
        if (announcerEnabled) {
            log.info("MiniMe:");
        }

        final List<Card> otherCards = new ArrayList<>(table.getFlippedCards());
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

    private Card flipCard(final Deck deck) {
        final Card card = deck.drawFromDeck();
        table.getFlippedCards().add(card);
        return card;
    }

    private void revealCards(final Player player) {
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
        log.info(OutCalculator.getAllOuts(player.getCards(),
                table.getFlippedCards(),
                deck.getFlippedDownDeck()).toString());
    }

}
