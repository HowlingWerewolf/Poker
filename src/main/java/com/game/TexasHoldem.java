package com.game;

import com.game.actor.Player;
import com.game.elements.HandEvaluator;
import com.game.playground.Board;
import com.game.playground.asset.Card;
import com.game.playground.Deck;
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
        me.getHand().add(deck.drawFromDeck());
        me.getHand().add(deck.drawFromDeck());
        Collections.sort(me.getHand());

        final Player miniMe = new Player();
        miniMe.getHand().add(deck.drawFromDeck());
        miniMe.getHand().add(deck.drawFromDeck());
        Collections.sort(miniMe.getHand());

        // we are too rookie showing each other's card
        log.info(me.getHand().toString());
        log.info(miniMe.getHand().toString());

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



        log.info("----------------------------");
        log.info("Me:");
        final List<Card> hand1 = new ArrayList<>(board.getFlippedCards());
        hand1.addAll(me.getHand());
        final HandEvaluator handEvaluator1 = new HandEvaluator(hand1);
        log.info(handEvaluator1.evaluate().getRanking().toString());

        log.info("----------------------------");
        log.info("MiniMe:");
        final List<Card> hand2 = new ArrayList<>(board.getFlippedCards());
        hand2.addAll(miniMe.getHand());
        final HandEvaluator handEvaluator2 = new HandEvaluator(hand2);
        log.info(handEvaluator2.evaluate().getRanking().toString());
    }

}
