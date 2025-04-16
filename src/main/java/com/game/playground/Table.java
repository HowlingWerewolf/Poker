package com.game.playground;

import com.game.actor.Player;
import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Log
public class Table implements Cloneable {
    final List<Card> flippedCards = new ArrayList<>(5);
    final List<Player> players = new ArrayList<>(8);
    final List<List<Card>> outs = new ArrayList<>(1000);
    final Deck deck;
    // TODO chips?

    public Table() {
        deck = new Deck(false);
    }

    public Table(boolean announcerEnabled) {
        deck = new Deck(announcerEnabled);
    }

    @Override
    public Table clone() {
        try {
            return (Table) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
