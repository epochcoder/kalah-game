
package com.github.epochcoder.kalah.game.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Willie Scholtz
 */
abstract class SeedAcceptor {


    private final Queue<Seed> seeds;

    public SeedAcceptor() {
        this.seeds = Queues.newArrayDeque();
    }

    public int amountOfSeeds() {
        return this.seeds.size();
    }

    /**
     * distributes a seed to the specified acceptor, returns true if the source/current pit is empty as a result of this operation
     * @param acceptor
     * @return
     */
    protected boolean distributeTo(final SeedAcceptor acceptor) {
        Preconditions.checkArgument(!this.equals(acceptor), "cannot distribute to myself");

        // get a lock on seeds before we remove an instance
        synchronized (this.seeds) {
            if (this.amountOfSeeds() > 0) {
                acceptor.accept(this.seeds.poll());
            }

            return this.amountOfSeeds() == 0;
        }
    }


    public abstract String getAcceptorId();


    protected int accept(final Seed seed) {
        Preconditions.checkNotNull(seed, "cannot accept a null seed!");

        synchronized (this.seeds) {
            this.seeds.offer(seed);

            return this.amountOfSeeds();
        }
    }

    @Override
    public int hashCode() {
        return 3 * this.getAcceptorId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final SeedAcceptor other = (SeedAcceptor) obj;
        return this.getAcceptorId().equals(other.getAcceptorId());
    }
}
