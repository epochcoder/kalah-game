package com.github.epochcoder.kalah.game.events;

import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;

/**
 *
 * @author Willie Scholtz
 */
public interface KalahListener {

    public void gameStart();
    public void gameEnd(final Player whoWon);

    public void sowStart(final Player player, final Pit fromPit);
    public void sowEnd(final Player player, final Pit fromPit);

    public void playerSwitch(final Player newPlayer);
    public void freeMove(final Player forPlayer);

    public void pitEmpty(Pit aThis);

    public void seedAdded(Player player, SeedAcceptor aThis, Seed seed);


}
