package com.github.epochcoder.kalah.game.events;

import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;

/**
 * Defines the Kalah Event System.
 * A Game manager should implement this listener to handle game specific events,
 * used mostly to update UI's
 * @author Willie Scholtz
 */
public interface KalahListener {

    /**
     * called when the game is in the started state.
     */
    public void gameStart();

    /**
     * called when the game has ended, the caller should have already determined the winner and loser
     * if the winner or loser is null, the game was tied
     * @param whoWon the player who won the game, or null for a tie
     * @param whoLost the player who lost the game,  or null for a tie
     */
    public void gameEnd(final Player whoWon, final Player whoLost);

    /**
     * called when the specified player has started sowing their seeds from the specified pit
     * @param player the player who started sowing
     * @param fromPit the pit that is being sown from
     */
    public void sowStart(final Player player, final Pit fromPit);

    /**
     * called when the specified player has completed sowing their seeds from the specified pit.
     * this does not include any special rules that may have kicked of as a result of the play.
     * the game may still end and {@link #freeMove(com.github.epochcoder.kalah.game.entity.Player)} may still be called
     * @param player the player who completed sowing
     * @param fromPit the pit that was being sown from
     */
    public void sowEnd(final Player player, final Pit fromPit);

    /**
     * called when a game round ends, and the previous player does not receive a free round.
     * @param newPlayer the player that will now be allowed to make the next move
     */
    public void playerSwitch(final Player newPlayer);

    /**
     * called when the specified player's last seeds has landed in his store.
     * he now receives a 'free' move, i.e the player does not change.
     * @param forPlayer the player that receives the free move
     */
    public void freeMove(final Player forPlayer);

    /**
     * called when the specified pit has been depleted of all seeds
     * @param pit the pit that is now empty
     */
    public void pitEmpty(final Pit pit);

    /**
     * called when an acceptor (either a pit or a store) receives a seed
     * @param acceptor the acceptor that has received the specified seed
     * @param seed the seed that has been added to the specified acceptor
     */
    public void seedAdded(final SeedAcceptor acceptor, final Seed seed);

}