package com.game.playground;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class Deck {
    final List<Card> cards = new ArrayList<>();
    final boolean announcerEnabled;

    public Deck() {
        announcerEnabled = true;
        createDeck(announcerEnabled);
    }

    public Deck(final boolean announcerEnabled) {
        this.announcerEnabled = announcerEnabled;
        createDeck(announcerEnabled);
    }

    private void createDeck(boolean announcerEnabled) {
        // create the cards
        for (Color color : Color.values()) {
            for (Value value : Value.values()) {
                cards.add(new Card(color, value));
            }
        }

        // shuffle the deck
        if (announcerEnabled) {
        log.info("Shuffling...");
        }
        Collections.shuffle(cards);
    }

    public Card drawFromDeck() {
        return cards.remove(0);
    }

    public List<Card> getFlippedDownDeck() {
        return cards;
    }

}
