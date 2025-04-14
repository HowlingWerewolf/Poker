package com.game;

import lombok.extern.java.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log
public class TexasHoldemParallelThreadPerformance {

    private static final Integer MAX = 500000;
    private static final Integer CHUNK = 100000;
    private static final Integer THREAD_COUNT = 5;

    public static void main(final String[] args) {
        final Instant start = Instant.now();
        final ConcurrentLinkedQueue<TexasGameSimulation> gamesToBePlayed = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<Thread> threadRegister = new ConcurrentLinkedQueue<>();
        for (int i = 1; i < MAX; i++) {
            final TexasGameSimulation game = new TexasGameSimulation(false, false, false);
            gamesToBePlayed.add(game);
            if (i % CHUNK == 0) {
                parallelPlay(gamesToBePlayed, threadRegister);
            }
        }

        // play the remainders from the chunk
        while (!gamesToBePlayed.isEmpty()) {
            parallelPlay(gamesToBePlayed, threadRegister);
        }

        final Instant finish = Instant.now();
        final long timeElapsed = Duration.between(start, finish).toMillis();
        log.info(MAX + " games generated in " + timeElapsed + " ms");
        log.info("Games played per second: " + MAX / timeElapsed * 1000);
    }

    /**
     * Parallel plays the gives queue.
     * Note: the game played seems to be so fast that it's not worth starting a thread for execution?
     * (see parallel stream performance in the other class)
     *
     * @param gamesToBePlayed games
     * @param threadRegister  the active threads
     */
    private static void parallelPlay(
            final AbstractQueue<TexasGameSimulation> gamesToBePlayed,
            final ConcurrentLinkedQueue<Thread> threadRegister) {

        do {
            for (int i = 0; i <= THREAD_COUNT; i++) {
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        final TexasGameSimulation game = gamesToBePlayed.poll();
                        if (game != null) {
                            threadRegister.add(this);
                            game.play();
                            threadRegister.remove(this);
                        }
                    }
                };
                t.start();
            }

            threadRegister.forEach(t -> {
                try {
                    t.wait();
                } catch (final IllegalMonitorStateException e) {
                    // don't care
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } while (!gamesToBePlayed.isEmpty());
    }

}
