package com.github.epochcoder.kalah.game.entity;

import com.google.common.base.Preconditions;

/**
 * a store that may contain an arbitrary number of seeds.
 * @author Willie Scholtz
 */
public final class Store extends SeedAcceptor {

    public Store(final Player player) {
        // substititue the playerId as store id since players only have one store
        super(player, player.getPlayerId());
    }

    @Override
    public void acceptorEmpty() {
        Preconditions.checkState(true, "store should never be empty "
                + "as a result of acceptorEmpty() call!");
    }

    @Override
    public boolean acceptorReceivedLast(final boolean normalMove) {
        final Player storeOwner = this.getPlayer();
        if (normalMove) {
            // if the last sown seed lands in the player's store, the player gets an additional move.
            storeOwner.getGame().getKalahListener().freeMove(storeOwner);

            // indicate to the caller we have another move
            return true;
        }

        return false;
    }


    @Override
    public String toString() {
        return "Store{"
                + "storeId=" + this.getPlayer().getPlayerId()
                + ", playerName=" + this.getPlayer().getPlayerName()
                + ", seedCnt=" + this.amountOfSeeds()
                + '}';
    }
}