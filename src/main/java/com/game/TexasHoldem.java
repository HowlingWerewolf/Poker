package com.game;

import com.game.actor.Player;
import com.game.elements.Hand;
import com.game.calculators.HandEvaluator;
import com.game.playground.Board;
import com.game.playground.Deck;
import com.game.playground.asset.Card;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class TexasHoldem {

    public static void main(String[] args) {
        final Board board = new Board();
        final Deck deck = new Deck();

        // deal
        log.info("Dealing...");
        final Player me = new Player();
        me.getCards().add(deck.drawFromDeck());
        me.getCards().add(deck.drawFromDeck());
        Collections.sort(me.getCards());

        final Player miniMe = new Player();
        miniMe.getCards().add(deck.drawFromDeck());
        miniMe.getCards().add(deck.drawFromDeck());
        Collections.sort(miniMe.getCards());

        // we are too rookie showing each other's card
        log.info(me.getCards().toString());
        log.info(miniMe.getCards().toString());

        // burn
        deck.drawFromDeck();

        // flop
        log.info("Flop!");
        board.getFlippedCards().add(deck.drawFromDeck());
        board.getFlippedCards().add(deck.drawFromDeck());
        board.getFlippedCards().add(deck.drawFromDeck());
        log.info(board.getFlippedCards().toString());

        // burn
        deck.drawFromDeck();

        // turn
        log.info("Turn!");
        final Card turnCard = deck.drawFromDeck();
        board.getFlippedCards().add(turnCard);
        log.info(turnCard.toString());

        // burn
        deck.drawFromDeck();

        // river
        log.info("River!");
        final Card riverCard = deck.drawFromDeck();
        board.getFlippedCards().add(riverCard);
        log.info(riverCard.toString());


        log.info("Me:");
        final List<Card> myCards = new ArrayList<>(board.getFlippedCards());
        myCards.addAll(me.getCards());
        final HandEvaluator myHandEvaluator = new HandEvaluator(myCards);
        final Hand myHand = myHandEvaluator.evaluate();
        log.info("My hand is: " + myHand.getCards().toString());
        log.info(myHand.getRanking().toString());
        log.info("My strongest combination is: " + myHand.getStrongestCombination().toString());

        log.info("MiniMe:");
        final List<Card> otherCards = new ArrayList<>(board.getFlippedCards());
        otherCards.addAll(miniMe.getCards());
        final HandEvaluator otherHandEvaluator = new HandEvaluator(otherCards);
        final Hand otherHand = otherHandEvaluator.evaluate();
        log.info("MiniMe's hand is: " + otherHand.getCards().toString());
        log.info(otherHand.getRanking().toString());
        log.info("MiniMe's strongest combination is: " + otherHand.getStrongestCombination().toString());

        if (myHand.compareTo(otherHand) > 0) {
            log.info("I won");
        } else if (myHand.compareTo(otherHand) == 0) {
            log.info("It's a draw");
        } else {
            log.info("Minime won");
        }
    }

}
