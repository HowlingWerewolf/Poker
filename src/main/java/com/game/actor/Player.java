package com.game.actor;

import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    final String uniqueName;
    final List<Card> cards;

    public Player(final String uniqueName) {
        this.uniqueName = uniqueName;
        cards = new ArrayList<>(2);
    }

    // for the one who sees everyone's card
    double winRatio = Double.NaN;

    // blindly guessing other's cards
    double blindWinRatio = Double.NaN;
}
