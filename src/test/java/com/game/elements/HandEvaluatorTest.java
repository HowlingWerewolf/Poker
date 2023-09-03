package com.game.elements;

import com.game.playground.asset.Card;
import com.game.playground.asset.Color;
import com.game.playground.asset.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.FLUSH, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.STRAIGHT, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.STRAIGHT, evaluated.getRanking());
    }

    @Test
    void testRoyalFlush() {
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.ROYAL_FLUSH, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.POKER, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.FULL_HOUSE, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.DRILL, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.TWO_PAIRS, evaluated.getRanking());
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
        Assertions.assertNotNull(evaluated);
        Assertions.assertNotNull(evaluated.getCards());
        Assertions.assertEquals(Ranking.ONE_PAIR, evaluated.getRanking());
    }

}
