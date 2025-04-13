package com.game.playground;

import com.game.actor.Player;
import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class Table {
    final List<Card> flippedCards = new ArrayList<>();
    final List<Player> players = new ArrayList<>();
    final List<List<Card>> outs = new ArrayList<>();
    final Deck deck;
    // TODO chips?

    public Table() {
        deck = new Deck(false);
    }

    public Table(boolean announcerEnabled) {
        deck = new Deck(announcerEnabled);
    }
}
