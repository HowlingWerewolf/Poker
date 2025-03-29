package com.game;

import lombok.extern.java.Log;

import java.time.Duration;
import java.time.Instant;

@Log
public class TexasHoldemPerformance {

    private static final Integer MAX = 100000;

    public static void main(final String[] args) {
        final Instant start = Instant.now();
        for (int i = 1; i < MAX; i++) {
            final TexasGameSimulation game = new TexasGameSimulation(false, false, false);
            game.play();
        }
        final Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.info(MAX + " games generated in " + timeElapsed + " ms");
        log.info("Games played per second: " + MAX / timeElapsed * 1000);
    }

}
