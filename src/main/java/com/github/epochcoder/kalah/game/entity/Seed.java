package com.github.epochcoder.kalah.game.entity;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents the in game currency, these seeds will
 * be placed in players houses or stores
 * @author Willie Scholtz
 */
public final class Seed {

    private static final Logger LOG = LoggerFactory.getLogger(Seed.class);

    /**
     * this counter hands out sequential id's to seeds,
     * we never reset it and don't mind the id's it gives,
     * we just need unique seed's per game
     */
    private static final AtomicInteger SEED_COUNTER = new AtomicInteger();

    /**
     * the player that this seed currently belongs to
     */
    private Player currentPlayer;

    private final int seedId;

    /**
     * creates a new seed with a unique id
     */
    public Seed() {
        this.seedId = SEED_COUNTER.getAndIncrement();
    }

    /**
     * sets the new owner of this seed
     * @param player the player to set the owner to
     */
    public synchronized void setCurrentPlayer(final Player player) {
        LOG.trace("seed[{}] changing owner to player[{}]", this, player);
        this.currentPlayer = player;
    }

    /**
     * retrieves the player that this seed belongs to
     * @return the current player, may be null
     */
    public synchronized Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.seedId;

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

        final Seed other = (Seed) obj;
        if (this.seedId != other.seedId) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Seed{"
                + "currentPlayer=" + currentPlayer
                + ", seedId=" + seedId
                + '}';
    }
}
