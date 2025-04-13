package com.game.actor;

import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Player {
    final List<Card> cards = new ArrayList<>();

    // the possible outcomes stored here
    final Map<List<Card>, List<Card>> outs = new HashMap<>();
    double winRatio = Double.NaN;
}
