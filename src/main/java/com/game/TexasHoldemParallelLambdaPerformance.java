package com.game;

import lombok.extern.java.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log
public class TexasHoldemParallelLambdaPerformance {

    private static final Integer MAX = 500000;
    private static final Integer CHUNK = 100000;

    public static void main(final String[] args) {
        final Instant start = Instant.now();
        final ConcurrentLinkedQueue<TexasGameSimulation> gamesToBePlayed = new ConcurrentLinkedQueue<>();
        for (int i = 1; i < MAX; i++) {
            final TexasGameSimulation game = new TexasGameSimulation(false, false, false);
            gamesToBePlayed.add(game);
            if (i % CHUNK == 0) {
                parallelPlay(gamesToBePlayed);
            }
        }

        // if there's leftovers due to chunk, play them as well
        parallelPlay(gamesToBePlayed);

        final Instant finish = Instant.now();
        final long timeElapsed = Duration.between(start, finish).toMillis();
        log.info(MAX + " games generated in " + timeElapsed + " ms");
        log.info("Games played per second: " + MAX / timeElapsed * 1000);
    }

    private static void parallelPlay(final ConcurrentLinkedQueue<TexasGameSimulation> gamesToBePlayed) {
        if (!gamesToBePlayed.isEmpty()) {
            gamesToBePlayed.stream()
                    .parallel()
                    .forEach(TexasGameSimulation::play);
            gamesToBePlayed.clear();
        }
    }

}
