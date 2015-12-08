package com.github.epochcoder.kalah.game.entity;

import com.github.epochcoder.kalah.game.Kalah;
import com.github.epochcoder.kalah.game.KalahException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a player of the Kalah game.
 * This class must be subclassed to provide play specific implementations.
 * @author Willie Scholtz
 */
public abstract class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private final Kalah game;
    private final Store store;
    private final Pit[] pits;

    private final int playerId;
    private final String playerName;

    /**
     * creates a new instance of a Player,
     * the player has an identifier and a friendly name
     * @param game the game that this player is associated with
     * @param playerId an identifier for this player
     * @param playerName a friendly display name for this player
     */
    public Player(final Kalah game, final int playerId, final String playerName) {
        // no specific validations on these parameters
        this.playerId = playerId;
        this.playerName = playerName;

        this.game = Preconditions.checkNotNull(game,
                "cannot create a Player without a valid Game!");

        LOG.debug("creating new store for player[{}]", playerId);
        // create a new empty store for this player, this store will keep
        // track of the players seeds, and inevitably his score
        this.store = new Store(this);

        LOG.debug("creating pits for player[{}]", playerId);
        // initialize this players pits with the configured amount of pits per side
        // using an array since we know this will be fixed size.
        this.pits = new Pit[this.game.getConfiguration().getPits()];
        for (int i = 0; i < this.pits.length; i++) {
            this.pits[i] = new Pit(this.game.getConfiguration(), this, i);
        }
    }

    /**
     * retrieves an immutable list of this player's pits
     * @return a non-null immutable list of this player's pits
     */
    protected final List<Pit> getPits() {
        return ImmutableList.copyOf(this.pits);
    }

    /**
     * retrieves this player's pit by the specified pitId
     * @param pitId a valid pitId
     * @return a valid pit, or null of none were found or an invalid id was supplied
     */
    protected final Pit getPitById(final int pitId) {
        Pit usePit = null;
        for (Pit pit : this.pits) {
            if (pit.getPitId() == pitId) {
                usePit = pit;
                break;
            }
        }

        return usePit;
    }

    /**
     * retrieves the current score for this player, it is calculated by
     * all the seeds he has in his store, and all of his pits.
     * @param includePitScore should the seeds in this player's pit be included in the score?
     * @param includeStoreScore should the seeds in this player's store be included in the score?
     * @return a score, the sum of all seeds in a player's house, and his pits
     */
    public final int getScore(final boolean includePitScore, final boolean includeStoreScore) {
        Preconditions.checkArgument(includePitScore || includeStoreScore,
                "need to include at least one score!");

        final List<SeedAcceptor> acceptors = Lists.newArrayList();
        if (includePitScore) {
            LOG.debug("including pit score for player[{}]", this.playerId);
            acceptors.addAll(Arrays.asList(this.pits));
        }

        if (includeStoreScore) {
            LOG.debug("including store score for player[{}]", this.playerId);
            acceptors.add(this.store);
        }

        int sum = 0;
        for (SeedAcceptor acceptor : acceptors) {
            sum += acceptor.amountOfSeeds();
        }

        return sum;
    }

    /**
     * main entry point into a players turn, subclasses must
     * override this to implement specific play semantics,
     * for instance a human player or or an AI player
     * @throws KalahException if any validation issues occur during play
     */
    public abstract void play() throws KalahException;

    /**
     * starts the sow process from the specified pitId
     * @param pitId the pitId to start sowing seeds from
     * @throws KalahException thrown when any game validation issues occur
     */
    protected final void sowFromPit(int pitId) throws KalahException {
        Preconditions.checkState(this.equals(this.game.getCurrentPlayer()), "player[" + this
                + "] is not the current player, and may not play this round!");

         // look for the pit that we should start sowing from
        final Pit usePit = this.getPitById(pitId);
        if (usePit == null) {
            throw new KalahException(KalahException.KalahProblem.INVALID_PIT);
        }

        // check if there are any seeds to sow4
        if (usePit.amountOfSeeds() == 0) {
            throw new KalahException(KalahException.KalahProblem.NO_SEEDS_IN_PIT);
        }

        final Player oppositePlayer = this.getOpponent();
        Preconditions.checkState(oppositePlayer != null && !oppositePlayer.equals(this),
                "invalid class state, could not determine opposite player!");

        // establish a game ring, the player will sow from his pit
        // around this ring until his seeds run out
        final LinkedList<SeedAcceptor> gameRing = Lists.newLinkedList();

        // add our own pits
        gameRing.addAll(this.getPits());

        // drop in our store, never in opponent's pit
        gameRing.add(this.getStore());

        // drop in opponent's pits
        gameRing.addAll(oppositePlayer.getPits());

        // let the listener know we are starting to sow
        this.game.getKalahListener().sowStart(this, usePit);

        // check if we have another move after this round
        boolean freeMove = false;

        // distribute all seeds around the ring
        SeedAcceptor nextAcceptor = null;
        while (usePit.amountOfSeeds() > 0) {
            // next acceptor will only be null on the first run, drop first seed in next pit, or the next acceptor
            final int currentIndex = gameRing.indexOf(nextAcceptor == null ? usePit : nextAcceptor);

            // get the next acceptor to use
            nextAcceptor = currentIndex == (gameRing.size() - 1)
                     // if we were at the end, jump to the first one
                    ? gameRing.getFirst()
                     // else we just get the next one in our ring
                    : gameRing.get(currentIndex + 1);

            // transfer :) our acceptors will handle game rules
            // freeMove can only possibly be true on the last
            // iteration so we don't mind overwriting it
            freeMove = usePit.distributeTo(nextAcceptor);
        }

        // let the listener know we have stopped sowing
        this.game.getKalahListener().sowEnd(this, usePit);

        // end of a turn, check if the game has ended (a player has no more seeds in a pit)
        // additionally, switch players, and updates scores
        Player endGamePlayer;
        if ((endGamePlayer = this.game.isEndOfGame()) != null) {
            // the other player moves all remaining seeds to their store,
            // and the player with the most seeds in their store wins.
            final Player endGameOpponent = endGamePlayer.getOpponent();
            final List<Pit> endGamePits = endGameOpponent.getPits();
            for (Pit pit : endGamePits) {
                // distribute all seeds to my store
                pit.distributeAll(endGameOpponent.getStore());
            }

            // determine who won the game?
            final Player playerOne = this.getGame().getPlayerOne();
            final Player playerTwo = this.getGame().getPlayerTwo();

            final int p1Score = playerOne.getScore(false, true);
            final int p2Score = playerTwo.getScore(false, true);

            this.game.endGame(p1Score > p2Score
                    ? playerOne // player one won!
                    : p1Score == p2Score
                            ? null // tie
                            : playerTwo); // player two won!);
        } else if (!freeMove) {
            // change the player to the other player
            this.game.setCurrentPlayer(oppositePlayer);
            this.game.getKalahListener().playerSwitch(oppositePlayer);
        }
    };

    /**
     * @return retrieves the identifier for this player
     */
    public final int getPlayerId() {
        return this.playerId;
    }

    /**
     * @return retrieves the name for this player
     */
    public final String getPlayerName() {
        return this.playerName;
    }

    /**
     * retrieves the game this player is currently playing
     * @return a valid non-null instance of the game
     */
    public final Kalah getGame() {
        return this.game;
    }

    /**
     * retrieves this player's Store
     * @return a valid non-null instance of this player's store
     */
    public final Store getStore() {
        return this.store;
    }

    /**
     * retrieves the opponent for this player
     * @return a valid non-null Player instance
     */
    public final Player getOpponent() {
        // determine which player we are dealing with here
        return this.equals(this.game.getPlayerOne())
                ? this.game.getPlayerTwo() : this.game.getPlayerOne();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.playerId;
        hash = 79 * hash + Objects.hashCode(this.playerName);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Player other = (Player) obj;
        if (this.playerId != other.playerId) {
            return false;
        }

        if (!Objects.equals(this.playerName, other.playerName)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Player{"
                + "playerId=" + this.playerId
                + ", playerName=" + this.playerName
                + '}';
    }
}