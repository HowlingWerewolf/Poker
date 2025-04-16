package com.game.playground;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Log
public class Deck {
    List<Card> cards = new ArrayList<>(52);
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
        for (final Color color : Color.values()) {
            for (final Value value : Value.values()) {
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
        return cards.removeFirst();
    }

    public List<Card> getFlippedDownDeck() {
        return cards;
    }

    @Override
    public Deck clone() throws CloneNotSupportedException {
        final Deck copy = (Deck) super.clone();
        copy.cards.clear();
        copy.cards.addAll(cards.stream().map(card -> {
            try {
                return card.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).toList());
        return copy;
    }

}
