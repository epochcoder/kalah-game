package com.github.epochcoder.kalah.game.entity.impl;

import com.github.epochcoder.kalah.game.Kalah;
import com.github.epochcoder.kalah.game.KalahException;
import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import java.util.Random;

/**
 * An implementation of a player that plays random moves
 * @author Willie Scholtz
 */
public class RandomPlayer extends Player {

    private final Random random;

    /**
     * creates a new random player
     * @param game the game to play in
     * @param playerId the id of the player
     * @param playerName the name of the player
     */
    public RandomPlayer(Kalah game, int playerId, String playerName) {
        super(game, playerId, playerName);

        // create a new random for this player
        this.random = new Random();
    }

    /**
     * returns a random pit number between zero and the configured amount of pits
     * @return a valid integer
     */
    private int getRandomPit() {
        return this.random.nextInt(this.getGame()
                .getConfiguration().getPits());
    }

    /*
     * automatically plays a move (no input required)
     * tries to prevent any exceptions by checing if it is a valid move and the pit is empty
     * @throws KalahException if any validation issues occur during play
     */
    public void play() throws KalahException {
        Pit usePit = null;
        do {
            usePit = this.getPitById(this.getRandomPit());
        } while (usePit.amountOfSeeds() == 0);
        this.sowFromPit(usePit.getPitId());
    }
}