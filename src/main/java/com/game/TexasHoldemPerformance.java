package com.game;

import lombok.extern.java.Log;

@Log
public class TexasHoldemPerformance {

    public static void main(String[] args) {
        for (int i = 1; i < 10000; i++) {
            log.info("-------------GAME " + i + "-------------");
            final TexasGameSimulation game = new TexasGameSimulation(true, false);
            game.play();
        }
    }

}
