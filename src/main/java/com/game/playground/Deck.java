package com.game.playground;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class Deck {
    final List<Card> deck = new ArrayList<>();

    public Deck() {
        // create the cards
        for (Color color : Color.values()) {
            for (Value value : Value.values()) {
                deck.add(new Card(color, value));
            }
        }

        // shuffle the deck
        log.info("Shuffling...");
        Collections.shuffle(deck);
    }

    public Card drawFromDeck() {
        return deck.remove(0);
    }
}
