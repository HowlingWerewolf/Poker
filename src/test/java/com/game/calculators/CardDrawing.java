package com.game.calculators;

import com.game.playground.Deck;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;

public interface CardDrawing {
    default Card findCardFromDeck(final Color color, final Value value, final Deck deck) {
        final Card card = Card.builder()
                .color(color)
                .value(value)
                .build();
        final int cardOneIndex = deck.getFlippedDownDeck().indexOf(card);
        return deck.getFlippedDownDeck().remove(cardOneIndex);
    }
}
