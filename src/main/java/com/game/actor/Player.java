package com.game.actor;

import com.game.playground.asset.Card;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Player {
    final List<Card> hand = new ArrayList<>();
    // chips?
}
