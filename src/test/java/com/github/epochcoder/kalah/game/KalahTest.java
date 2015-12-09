package com.github.epochcoder.kalah.game;

import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;
import com.github.epochcoder.kalah.game.events.KalahListener;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Tests the complete Kalah game according to known 6,3 plays.
 *
 * All tests are executed in the order they appear in the source because of their naming.
 * A, B, C, etc.
 * @author Willie Scholtz
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KalahTest {

    private final KalahConfiguration gameConfig = new KalahConfiguration(6, 3);
    private final Kalah game = new Kalah(this.gameConfig, new NullListener());
    private final Player pOne = new ArgumentPlayer(this.game, 1, "Player 1");
    private final Player pTwo = new ArgumentPlayer(this.game, 2, "Player 2");

    public KalahTest() {
    }

    /**
     * tests that the current game has no players set
     */
    @Test
    public void aTestNoPlayers() {
        try {
            this.game.getCurrentPlayer();
            fail("Should have thrown InvalidArgumentException, but did not!");
        } catch (final Exception e) {
            assertTrue("incorrect exception!", e.getMessage().startsWith("no players set!"));
        }
    }

    /**
     * tests that the first player is has been set
     */
    @Test
    public void bTestSetFirstPlayer() {
//        this.game.setPlayerOne(new Player(game, 0, playerName) {
//            @Override
//            public void play() throws KalahException {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        });
//        try {
//            this.game.setgetCurrentPlayer();
//            fail("Should have thrown InvalidArgumentException, but did not!");
//        } catch (final Exception e) {
//            assertTrue("incorrect exception!", e.getMessage().startsWith("no players set!"));
//        }
    }

    private static class ArgumentPlayer extends Player {
        public ArgumentPlayer(Kalah game, int id, String name) {
            super(game, id, name);
        }
    }

    private static class NullListener implements KalahListener {
        @Override
        public void gameStart() {}

        @Override
        public void gameEnd(Player whoWon, Player whoLost) {}

        @Override
        public void sowStart(Player player, Pit fromPit) {}

        @Override
        public void sowEnd(Player player, Pit fromPit) {}

        @Override
        public void playerSwitch(Player newPlayer) {}

        @Override
        public void freeMove(Player forPlayer) {}

        @Override
        public void pitEmpty(Pit pit) {}

        @Override
        public void seedAdded(SeedAcceptor acceptor, Seed seed) {}
    }
}
