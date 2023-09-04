package com.game.elements;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class HandTest {

    @Test
    void testEquals() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.FOUR),
                new Card(Color.CLUB, Value.FIVE),
                new Card(Color.CLUB, Value.JACK)
        );
        final Hand hand = new Hand(cards);
        final Hand otherHand = new Hand(cards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(0, result);
    }

    @Test
    void testEqualsNegative() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.FOUR),
                new Card(Color.CLUB, Value.FIVE),
                new Card(Color.CLUB, Value.JACK)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.FOUR),
                new Card(Color.SPADE, Value.FIVE),
                new Card(Color.SPADE, Value.JACK)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testEqualsPositive() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.CLUB, Value.JACK)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.FOUR),
                new Card(Color.SPADE, Value.FIVE),
                new Card(Color.SPADE, Value.JACK)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsHighCardKickerDecider() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.HEART, Value.TEN),
                new Card(Color.SPADE, Value.SEVEN),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.HEART, Value.TEN),
                new Card(Color.SPADE, Value.SEVEN),
                new Card(Color.CLUB, Value.TWO)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsOnePair() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.HEART, Value.THREE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(0, result);
    }

    @Test
    void testEqualsStrongerOnePair() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.HEART, Value.THREE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsOnePairKickerDecider() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.DIAMOND, Value.JACK),
                new Card(Color.DIAMOND, Value.TWO)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsStrongerTwoPair_TheSecondIsBetter() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.HEART, Value.THREE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsStrongerTwoPair_TheFirstIsBetter() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.JACK),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.KING),
                new Card(Color.HEART, Value.KING),
                new Card(Color.DIAMOND, Value.JACK),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.HEART, Value.THREE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsTwoPairKickerDecider() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.TEN),
                new Card(Color.SPADE, Value.TEN),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.DIAMOND, Value.TEN),
                new Card(Color.DIAMOND, Value.TEN),
                new Card(Color.DIAMOND, Value.TWO)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsDrillKickerDecider() {
        // normally this won't happen in texas holdem, but I wanted to create a general check
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.TEN),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.DIAMOND, Value.TEN),
                new Card(Color.DIAMOND, Value.TWO)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsDrill_TheFirstIsStronger() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.TEN),
                new Card(Color.CLUB, Value.THREE)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.SPADE, Value.KING),
                new Card(Color.HEART, Value.KING),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.DIAMOND, Value.TEN),
                new Card(Color.DIAMOND, Value.THREE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsStraight_TheFirstIsStronger() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.HEART, Value.QUEEN),
                new Card(Color.SPADE, Value.JACK),
                new Card(Color.CLUB, Value.TEN)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.DIAMOND, Value.QUEEN),
                new Card(Color.HEART, Value.JACK),
                new Card(Color.SPADE, Value.TEN),
                new Card(Color.CLUB, Value.NINE)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsFlush_TheFirstIsStronger() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.CLUB, Value.TWO),
                new Card(Color.CLUB, Value.TEN)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.NINE),
                new Card(Color.CLUB, Value.EIGHT),
                new Card(Color.CLUB, Value.SEVEN),
                new Card(Color.CLUB, Value.SIX)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

    @Test
    void testEqualsPokerKickerDecider() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.CLUB, Value.JACK)
        );
        final Hand hand = new Hand(cards);

        final List<Card> otherCards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.CLUB, Value.TEN)
        );
        final Hand otherHand = new Hand(otherCards);

        // when
        final int result = hand.compareTo(otherHand);

        // then
        Assertions.assertEquals(1, result);
    }

}
