package com.github.epochcoder.kalah.game.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Objects;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages seeds internally and should be subclassed to
 * provide specific implementations of seeds should be handled
 * @author Willie Scholtz
 */
public abstract class SeedAcceptor {

    private static final Logger LOG = LoggerFactory.getLogger(SeedAcceptor.class);

    private final Player player;
    private final Queue<Seed> seeds;
    private final String acceptorId;

    /**
     * constructs a new SeedAcceptor with the specified player a base
     * @param player the player to construct this acceptor with
     * @param acceptorId the id to use as a base for this acceptor
     */
    public SeedAcceptor(final Player player, int acceptorId) {
        this.player = Preconditions.checkNotNull(player,
                "cannot construct a SeedAcceptor witout a Player!");
        this.seeds = Queues.newConcurrentLinkedQueue();

        // acceptors need to be unique per game, this is accomplished
        // by assigning acceptors per player
        this.acceptorId = "acceptor_" + this.getClass().getSimpleName().toLowerCase()
                + "_pId" + this.player.getPlayerId() + "_aId" + String.valueOf(acceptorId);
    }

    /**
     * called when this acceptor has been exhausted by the
     * {@link #distributeTo(SeedAcceptor) distributeTo} call
     */
    public abstract void acceptorEmpty();

    /**
     * called when this acceptor has received the last seed from a source
     * @param normalMove indicates whether this call occurred as a result of a "normal move",
     * or a special move, such as taking an opponent's seeds
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    public abstract boolean acceptorReceivedLast(final boolean normalMove);

    /**
     * @return retrieves the amount of seeds within this queue
     */
    public final int amountOfSeeds() {
        return this.seeds.size();
    }

    /**
     * distributes all seeds to the specified acceptor, calls {@link #acceptorEmpty()}
     * if the current acceptor is empty as a result of this operation
     * @param acceptor the acceptor to distribute the seeds to
     */
    protected final void distributeAll(final SeedAcceptor acceptor) {
        LOG.debug("distibuting all seeds from "
                + "acceptor[{}] to acceptor[{}]", this, acceptor);

        while (!this.seeds.isEmpty()) {
            this.distributeTo(acceptor, false);
        }
    }

    /**
     * distributes a seed to the specified acceptor, calls {@link #acceptorEmpty()}
     * if the current acceptor is empty as a result of this operation
     * @param acceptor the acceptor to distribute the seeds to
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    protected final boolean distributeTo(final SeedAcceptor acceptor) {
        return this.distributeTo(acceptor, true);
    }

    /**
     * distributes a seed to the specified acceptor, calls {@link #acceptorEmpty()}
     * if the current acceptor is empty as a result of this operation
     * @param acceptor the acceptor to distribute the seeds to
     * @param normalMove indicates whether this call occurred as a result of a "normal move",
     * or a special move, such as taking an opponent's seeds
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    private boolean distributeTo(final SeedAcceptor acceptor, final boolean normalMove) {
        Preconditions.checkArgument(!this.equals(acceptor), "cannot distribute seeds to ourselves!");

        boolean anotherTurn = false;
        if (this.seeds.size() > 0) {
            final Seed transferSeed = this.seeds.poll();
            Preconditions.checkState(transferSeed != null,
                    "seed to transfer was null!");

            LOG.trace("distibuting seed[{}] from acceptor[{}] to acceptor[{}]",
                    transferSeed, this.acceptorId, acceptor.acceptorId);

            // send the seed to the source acceptor
            acceptor.accept(transferSeed);

            if (this.seeds.isEmpty()) {
                LOG.trace("acceptor[{}] is now empty, acceptor[{}] received the last seed[{}]",
                        this.acceptorId, acceptor.acceptorId, transferSeed);

                // let the source know it is empty
                this.acceptorEmpty();

                // this distribute call just removed the last seed,
                // let the destination know
                anotherTurn = acceptor.acceptorReceivedLast(normalMove);
            }
        }

        return anotherTurn;
    }

    /**
     * accepts a seed into this acceptor
     * @param seed a valid non-null seed
     */
    protected final void accept(final Seed seed) {
        Preconditions.checkNotNull(seed, "cannot accept a null seed!");

        LOG.trace("acceptor[{}] accepting seed[{}]", this.acceptorId, seed);

        // set the current player for the seed
        seed.setCurrentPlayer(this.getPlayer());

        // add the new seed to this acceptor
        this.seeds.offer(seed);

        // let the game listener know this acceptor received a seed
        this.getPlayer().getGame().getKalahListener()
                .seedAdded(this, seed);
    }

    /**
     * retrieves the player that owns this SeedAcceptor
     * @return a valid non-null instance of a Player
     */
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.player);
        hash = 71 * hash + Objects.hashCode(this.acceptorId);

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

        final SeedAcceptor other = (SeedAcceptor) obj;
        if (!Objects.equals(this.acceptorId, other.acceptorId)) {
            return false;
        }

        if (!Objects.equals(this.player, other.player)) {
            return false;
        }

        return true;
    }
}
