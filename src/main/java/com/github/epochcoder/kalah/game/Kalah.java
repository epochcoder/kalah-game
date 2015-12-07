package com.github.epochcoder.kalah.game;

import com.github.epochcoder.kalah.game.events.KalahListener;
import com.github.epochcoder.kalah.game.entity.Player;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * represents an instance of a kalah game
 * @author Willie Scholtz
 * @see https://en.wikipedia.org/wiki/Kalah
 * @see http://kalaha.krus.dk/
 */
public final class Kalah {

    private static final Logger LOG = LoggerFactory.getLogger(Kalah.class);

    private final Player playerOne;
    private final Player playerTwo;
    private final KalahListener listener;
    private final KalahConfiguration configuration;

    /**
     * constructs a new instance of the Kalah game with the
     * minimum amount of requirements required for play.
     * @param configuration the configuration for this game of Kalah
     * @param listener the listener used to keep track of events within the game
     * @param playerOne the first player for this game
     * @param playerTwo the second player for this game
     */
    public Kalah(final KalahConfiguration configuration, final KalahListener listener,
            final Player playerOne, final Player playerTwo) {
        final String messBase = "cannot construct an instance of Kalah";
        final String mess1 = messBase + " without a configuration instance!";
        final String mess2 = messBase + " without a valid game manager!";
        final String mess3 = messBase + " without two valid players!";
        final String mess4 = messBase + " without unique players!";

        this.configuration = Preconditions.checkNotNull(configuration, mess1);
        this.listener = Preconditions.checkNotNull(listener, mess2);
        this.playerOne = Preconditions.checkNotNull(playerOne, mess3);
        this.playerTwo = Preconditions.checkNotNull(playerTwo, mess3);

        // ensure our players have unique id's, we need to determine opposing players
        Preconditions.checkState(!this.playerOne.equals(this.playerTwo), mess4);
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
     * @return an instance of the configuration options
     */
    public KalahConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * retrieves the opponent for the specified player
     * @param player the player to retrieve an opponent for
     * @return a valid non-null Player instance
     */
    Player getOpponent(final Player player) {
        // determine which player we are dealing with here
        // package access as the public api has access to player one and two
        return player.equals(this.playerOne) ? this.playerTwo : this.playerOne;
    }

    /**
     * retrieves an instance of the Kalah game listener
     * @return an instance
     */
    KalahListener getKalahListener() {
        // package access, don't allow public to access this
        // we don't want client code calling internal functions
        return this.listener;
    }
}