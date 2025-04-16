package com.game.calculators;

import com.game.elements.Hand;
import com.game.elements.Ranking;
import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * All tests are written to get the strongest 5 card combination out of 7 cards.
 */
class HandEvaluatorTest {

    @Test
    void testFlush() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.FOUR),
                new Card(Color.CLUB, Value.FIVE),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.HEART, Value.KING)
        );
        final HandEvaluator evaluator = new HandEvaluator(cards);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.FLUSH);
    }

    @Test
    void testLowestStraight() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.HEART, Value.THREE),
                new Card(Color.SPADE, Value.FOUR),
                new Card(Color.CLUB, Value.FIVE),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT);
    }

    @Test
    void testHighestStraight() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.THREE),
                new Card(Color.SPADE, Value.FOUR),
                new Card(Color.SPADE, Value.TEN),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT);
    }

    @Test
    void testStraightFlush() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.THREE),
                new Card(Color.SPADE, Value.FOUR),
                new Card(Color.CLUB, Value.TEN),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT_FLUSH);
    }

    @Test
    void testStraightFlushTricky() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.SPADE, Value.QUEEN),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.TEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT_FLUSH);
    }

    @Test
    void testStraightFlushTricky2() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.HEART, Value.KING),
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.SPADE, Value.QUEEN),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.TEN),
                new Card(Color.CLUB, Value.NINE)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT_FLUSH);
    }

    @Test
    void testStraightFlushTricky3() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.SPADE, Value.QUEEN),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.TEN),
                new Card(Color.CLUB, Value.NINE),
                new Card(Color.CLUB, Value.EIGHT)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.STRAIGHT_FLUSH);
    }

    @Test
    void testPoker() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.POKER);
    }

    @Test
    void testFullHouse() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.THREE),
                new Card(Color.DIAMOND, Value.THREE),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.FULL_HOUSE);
    }

    @Test
    void testFullHouseTricky() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.HEART, Value.KING),
                new Card(Color.DIAMOND, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.SPADE, Value.ACE),
                new Card(Color.DIAMOND, Value.QUEEN),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.FULL_HOUSE);
    }

    @Test
    void testDrill() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.DRILL);
    }

    @Test
    void testDrillTricky() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.DIAMOND, Value.ACE),
                new Card(Color.HEART, Value.ACE),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.DRILL);
    }

    @Test
    void testTwoPairs() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.THREE),
                new Card(Color.DIAMOND, Value.THREE),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.TWO_PAIRS);
    }

    @Test
    void testPair() {
        // given
        final List<Card> hand = List.of(
                new Card(Color.CLUB, Value.KING),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.HEART, Value.KING),
                new Card(Color.SPADE, Value.THREE),
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN)
        );
        final HandEvaluator evaluator = new HandEvaluator(hand);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.ONE_PAIR);
    }

    @Test
    void testHighCard() {
        // given
        final List<Card> cards = List.of(
                new Card(Color.DIAMOND, Value.TWO),
                new Card(Color.CLUB, Value.FOUR),
                new Card(Color.CLUB, Value.ACE),
                new Card(Color.SPADE, Value.EIGHT),
                new Card(Color.CLUB, Value.JACK),
                new Card(Color.CLUB, Value.QUEEN),
                new Card(Color.HEART, Value.KING)
        );
        final HandEvaluator evaluator = new HandEvaluator(cards);

        // when
        final Hand evaluated = evaluator.evaluate();

        // then
        assertHand(evaluated, Ranking.HIGH_CARD);
        assertEquals(Value.ACE, evaluated.getStrongestCombination().getFirst().value());
    }

    private void assertHand(final Hand evaluated, final Ranking ranking) {
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        assertEquals(5, evaluated.getStrongestCombination().size());
        assertEquals(ranking, evaluated.getRanking());
    }

}
