package com.github.epochcoder.kalah.game;

import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;
import com.github.epochcoder.kalah.game.events.KalahListener;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests the complete Kalah game according to known 6,3 plays.
 * @author Willie Scholtz
 */
public class KalahTest {

    private Kalah createGame(boolean addPlayerOne, boolean addPlayerTwo) {
        final KalahConfiguration gameConfig = new KalahConfiguration(6, 3);
        final Kalah game = new Kalah(gameConfig, new NullListener());

        if (addPlayerOne) {
            final ArgumentPlayer pOne = new ArgumentPlayer(game, 1, "Player 1");
            assertTrue("player is already set!", game.getPlayerOne() == null);
            game.setPlayerOne(pOne);
        }

        if (addPlayerTwo) {
            final ArgumentPlayer pTwo = new ArgumentPlayer(game, 2, "Player 2");
            assertTrue("player is already set!", game.getPlayerTwo() == null);
            game.setPlayerTwo(pTwo);
        }

        return game;
    }

    @Test
    public void testNoPlayers() {
        Kalah game = createGame(false, false);
        try {
            game.getCurrentPlayer();
            fail("Should have thrown InvalidArgumentException, but did not!");
        } catch (final Exception e) {
            assertTrue("incorrect exception!", e.getMessage().startsWith("no players set!"));
        }
    }

    @Test
    public void testPlayerOne() {
        Kalah game = createGame(true, false);

        assertTrue("player does not have a store!",
                game.getPlayerOne().getStore() != null);

        Assert.assertEquals("player pits initialized incorrectly",
                game.getConfiguration().getPits() * game.getConfiguration().getSeeds(),
                game.getPlayerOne().getScore(true, true));
    }

    @Test
    public void testPlayerTwo() {
        Kalah game = createGame(false, true);

        assertTrue("player does not have a store!",
                game.getPlayerTwo().getStore() != null);

        Assert.assertEquals("player pits initialized incorrectly",
                game.getConfiguration().getPits() * game.getConfiguration().getSeeds(),
                game.getPlayerTwo().getScore(true, true));
    }


    @Test
    public void testOpponent() {
        Kalah game = createGame(true, true);

        assertTrue("player one does not have opponent!",
                game.getPlayerOne().getOpponent() != null);
        assertTrue("player two does not have opponent!",
                game.getPlayerOne().getOpponent() != null);

        assertTrue("player one has invalid opponent!",
                game.getPlayerOne().getOpponent().equals(game.getPlayerTwo()));
        assertTrue("player two has invalid opponent!",
                game.getPlayerTwo().getOpponent().equals(game.getPlayerOne()));
    }

    // --------------------------------- HELPER CLASSES -----------------------------------

    private static class ArgumentPlayer extends Player {
        public ArgumentPlayer(Kalah game, int id, String name) {
            super(game, id, name);
        }

        public void playFromArgument(int pitId) throws KalahException {
            this.sowFromPit(pitId);
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
