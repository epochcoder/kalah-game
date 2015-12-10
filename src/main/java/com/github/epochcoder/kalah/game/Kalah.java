package com.github.epochcoder.kalah.game;

import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.events.KalahListener;
import com.google.common.base.Preconditions;

/**
 * represents an instance of a kalah game
 * @author Willie Scholtz
 * @see https://en.wikipedia.org/wiki/Kalah
 * @see http://kalaha.krus.dk/
 */
public final class Kalah {

    private final transient KalahListener listener;
    private final KalahConfiguration configuration;

    private boolean started = false;
    private Player currentPlayer;
    private Player playerOne;
    private Player playerTwo;

    /**
     * constructs a new instance of the Kalah game with the
     * minimum amount of requirements required for play.
     * @param configuration the configuration for this game of Kalah
     * @param listener the listener used to keep track of events within the game
     */
    public Kalah(final KalahConfiguration configuration, final KalahListener listener) {
        final String messBase = "cannot construct an instance of Kalah";
        final String mess1 = messBase + " without a configuration instance!";
        final String mess2 = messBase + " without a valid game manager!";

        this.configuration = Preconditions.checkNotNull(configuration, mess1);
        this.listener = Preconditions.checkNotNull(listener, mess2);
    }

    /**
     * starts this game of Kalah
     */
    public void startGame() {
        Preconditions.checkState(!this.started, "game has already started!");
        this.started = true;

        this.getKalahListener().gameStart();
    }

    /**
     * ends this game of Kalah with the results of the game.
     * @param whoWon the player who won this game, null if the game was tied
     * @param whoLost the player who lost this game, null if the game was tied
     */
    public void endGame(final Player whoWon, final Player whoLost) {
        Preconditions.checkState(this.started, "game has not been started yet!");
        this.started = false;

        this.getKalahListener().gameEnd(whoWon, whoLost);
    }

    /**
     * checks if the current game has ended, the game has ended when one
     * player no longer has any seeds in any of their pits.
     * @return the player that no longer has any seeds left, or null
     */
    public Player isEndOfGame() {
        final Player currPlayer = this.getCurrentPlayer();
        final Player opponent = currPlayer.getOpponent();

        // we need to find out who played last to figure out who to give the win to.
        if (currPlayer.getScore(true, false) == 0) {
            return currPlayer;
        // it's not the current player, check his opponent
        } else if (opponent.getScore(true, false) == 0) {
            return opponent;
        } else {
            return null;
        }
    }

    /**
     * sets the first player of this game, players
     * must be unique and may only be set once
     * @param playerOne a valid Player instance
     */
    public void setPlayerOne(final Player playerOne) {
        // players may only be set once
        Preconditions.checkState(this.playerOne == null, "game already has a player one!");
        Preconditions.checkState(!this.started, "game has already started!");

        this.playerOne = Preconditions.checkNotNull(playerOne, "cannot set a null player!");

        // in cases where set playerTwo was called first
        if (this.playerTwo != null) {
            // ensure our players have unique id's, we need to determine opposing players
            Preconditions.checkState(!this.playerOne.equals(this.playerTwo),
                    "game needs to have unique players!");
        }
    }

    /**
     * sets the second player of this game, players
     * must be unique and may only be set once
     * @param playerTwo a valid Player instance
     */
    public void setPlayerTwo(final Player playerTwo) {
        // players may only be set once
        Preconditions.checkState(this.playerTwo == null, "game already has a player two!");
        Preconditions.checkState(!this.started, "game has already started!");

        this.playerTwo = Preconditions.checkNotNull(playerTwo, "cannot set a null player!");

        // in cases where set playerOne was called first
        if (this.playerOne != null) {
            // ensure our players have unique id's, we need to determine opposing players
            Preconditions.checkState(!this.playerOne.equals(this.playerTwo),
                    "game needs to have unique players!");
        }
    }

    /**
     * sets the current player of the Kalah game
     * @param player the current player
     */
    public synchronized void setCurrentPlayer(final Player player) {
        this.currentPlayer = player;
    }

    /**
     * @return an instance of the first player
     */
    public Player getPlayerOne() {
        return this.playerOne;
    }

    /**
     * @return an instance of the second player
     */
    public Player getPlayerTwo() {
        return this.playerTwo;
    }

    /**
     * @return the current player of the game,
     * always start with player one
     */
    public synchronized Player getCurrentPlayer() {
        Preconditions.checkState(this.playerOne != null
                && this.playerTwo != null, "no players set!");

        if (this.currentPlayer == null) {
            this.currentPlayer = this.playerOne;
        }

        return this.currentPlayer;
    }

    /**
     * @return an instance of the configuration options
     */
    public KalahConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * retrieves an instance of the Kalah game listener
     * @return a valid instance of the game's listener
     */
    public KalahListener getKalahListener() {
        return this.listener;
    }

    /**
     * @return a JSON representation of the entire game state
     */
    @Override
    public String toString() {
        return KalahSerializer.serializeGame(this);
    }
}