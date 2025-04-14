package com.game.actor;

import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    final List<Card> cards = new ArrayList<>(2);
    double winRatio = Double.NaN;
}
