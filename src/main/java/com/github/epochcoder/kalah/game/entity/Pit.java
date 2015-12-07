package com.github.epochcoder.kalah.game.entity;

import com.github.epochcoder.kalah.game.KalahConfiguration;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents a Pit that belongs to a Player,
 * this Pit contains the configured amount of seeds on creation
 * A player may operate on this pit to add/remove any seeds it may have
 * @author Willie Scholtz
 */
final class Pit extends SeedAcceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Pit.class);

    private final KalahConfiguration configuration;
    private final int pitId;

    public Pit(final KalahConfiguration configuration, final int pitId) {
        this.configuration = Preconditions.checkNotNull(configuration,
                "cannot create a Player without a valid game configuration!");
        this.pitId = pitId;

        final int amtSeeds = this.configuration.getSeeds();

        // populate the internal seed list as this is a new object, and will only be created for a new game
        for (int i = 0; i < amtSeeds; i++) {
            this.accept(new Seed());
        }
    }

    @Override
    public String toString() {
        return "Pit{"
                + ", pitId=" + this.getPitId()
                + ", seedsCnt=" + this.amountOfSeeds()
                + '}';
    }

    /**
     * @return the pitId
     */
    public int getPitId() {
        return this.pitId;
    }

    @Override
    public String getAcceptorId() {
        return "pit_" + this.pitId;
    }
}