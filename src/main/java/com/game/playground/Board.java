package com.game.playground;

import com.game.playground.asset.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Board {
    final List<Card> flippedCards = new ArrayList<>();
    // chips?
}
