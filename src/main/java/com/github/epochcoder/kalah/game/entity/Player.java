package com.github.epochcoder.kalah.game.entity;

import com.github.epochcoder.kalah.game.Kalah;
import com.github.epochcoder.kalah.game.KalahConfiguration;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Willie Scholtz
 */
public abstract class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private final KalahConfiguration configuration;
    private final Store store;
    private final Pit[] pits;

    private final int playerId;

    public Player(final KalahConfiguration configuration, final int playerId) {
        this.configuration = Preconditions.checkNotNull(configuration,
                "cannot create a Player without a valid game configuration!");
        this.playerId = playerId;

        // create a new empty store for this player, this store will keep
        // track of the players seeds, and inevitably his score
        this.store = new Store();

        // initialize this players pits with the configured amount of pits per side
        // using an array since we know this will be fixed size.
        this.pits = new Pit[this.configuration.getPits()];
        for (int i = 0; i < this.pits.length; i++) {
            this.pits[i] = new Pit(this.configuration, i);
        }

    }

    public int getScore() {
        final List<SeedAcceptor> acceptors = Lists.newArrayList();
        acceptors.addAll(Arrays.asList(this.pits));
        acceptors.add(this.store);

        int sum = 0;
        for (SeedAcceptor acceptor : acceptors) {
            sum += acceptor.amountOfSeeds();
        }

        return sum;

    }

    /**
     * main entry point into a players turn
     */
    public abstract void play();

    protected void sowFromPit(int pitId) {
        Pit usePit = null;
        for (Pit pit : this.pits) {
            if (pit.getPitId() == pitId) {
                // start sowing from here
                usePit = pit;
                break;
            }
        }

        Preconditions.checkState(usePit != null);

        boolean firstCycle;
        while (usePit.amountOfSeeds() > 0) {
            if (seedsToDistribute > 0) {
                for (int i = usePit.getPitId() + 1; i < this.pits.length; i++) {
                    final Pit sowTo = this.pits[i];

                    if (usePit.distributeTo(sowTo)) {
                        // last one sowed

                        break;
                    };

                }
            } else {
                // invalid move
            }
        }



        //usePit.sow();
    };

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.playerId;

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Player other = (Player) obj;
        return this.playerId == other.playerId;
    }

    @Override
    public String toString() {
        return "Player{"
                + "playerId=" + this.playerId
                + "playerScore=" + this.getScore()
                + '}';
    }





}
