package com.github.epochcoder.kalah.game.entity;

import com.github.epochcoder.kalah.game.KalahConfiguration;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a <tt>Pit</tt> that belongs to a <tt>Player</tt>.
 * this <tt>Pit</tt> contains the configured amount of seeds on creation
 * A <tt>Player</tt> may operate on this pit to add/remove any seeds as a
 * <tt>Pit</tt> is an instance of a <tt>SeedAcceptor</tt>
 * @author Willie Scholtz
 */
public final class Pit extends SeedAcceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Pit.class);

    private final KalahConfiguration configuration;
    private final int pitId;

    /**
     * creates a new pit that belongs to the specified player
     * @param configuration the current game configuration
     * @param player the player that this pit belongs to
     * @param pitId the id of this pit
     */
    public Pit(final KalahConfiguration configuration, final Player player, final int pitId) {
        super(player, pitId);

        this.configuration = Preconditions.checkNotNull(configuration,
                "cannot create a Player without a valid game configuration!");
        this.pitId = pitId;

        // ensure correct pitId was supplied
        Preconditions.checkArgument(pitId >= 0 && pitId <= KalahConfiguration.MAX_SEEDS - 1,
                "invalid pitId[%s] supplied, must be between 0 and %s", pitId, 0, KalahConfiguration.MAX_SEEDS - 1);

        final int amtSeeds = this.configuration.getSeeds();

        // populate the internal seed list as this is a new object, and will only be created for a new game
        for (int i = 0; i < amtSeeds; i++) {
            final Seed newSeed = new Seed();
            newSeed.setCurrentPlayer(player);

            // accept this seed into the pit
            this.accept(newSeed);
        }
    }

    /**
     * @return this pit's identifier
     */
    public int getPitId() {
        return this.pitId;
    }

    /**
     * retrieves the pit opposite this one on the game board.
     * @param oppositePlayer the player to use for retrieving the opposite pit
     * @return a valid non-null instance of a Pit
     */
    public Pit getOppositePit(final Player oppositePlayer) {
        // take the amount of configured pits and subtract the
        // current pitId and 1 (zero based id's)
        // to retrieve the pit opposite this one
        return oppositePlayer.getPitById(this.configuration.getPits() - this.getPitId() - 1);
    }

    @Override
    public void acceptorEmpty() {
        this.getPlayer().getGame()
                .getKalahListener().pitEmpty(this);
    }

    /**
     * If the last sown seed lands in an empty house owned by the player,
     * and the opposite house contains seeds, both the last seed and the opposite
     * seeds are captured and placed into the player's store.
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    @Override
    public boolean acceptorReceivedLast(final boolean normalMove) {
        final Player pitOwner = this.getPlayer();
        final Player currentPlayer = pitOwner
                .getGame().getCurrentPlayer();

        // if we have one seed, we just received another pit's last seed
        if (pitOwner.equals(currentPlayer) && this.amountOfSeeds() == 1) {
            final Pit oppositePit = this.getOppositePit(
                    this.getPlayer().getOpponent());

            // if the opposite pit has seeds, we transfer ours and theirs to our store
            if (oppositePit.amountOfSeeds() > 0) {
                final Store store = pitOwner.getStore();

                LOG.debug("taking all seeds from pit[{}] and transferring to player[{}] store",
                        oppositePit, pitOwner.getPlayerName());

                // take our new seed and the opponent's seeds and transfer to our store
                oppositePit.distributeAll(store);
                this.distributeTo(store);
            }
        }

        // I have no idea if we should generate free moves from pits?
        // not going to give a free move here, according to another game i played online
        return false;
    }

    @Override
    public String toString() {
        return "Pit{"
                + "pitId=" + this.getPitId()
                + ", playerId=" + this.getPlayer().getPlayerName()
                + ", seedsCnt=" + this.amountOfSeeds()
                + '}';
    }
}