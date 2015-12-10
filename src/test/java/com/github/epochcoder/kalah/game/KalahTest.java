package com.github.epochcoder.kalah.game;

import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;
import com.github.epochcoder.kalah.game.events.KalahListener;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the complete Kalah game according to known 6,3 plays.
 * @author Willie Scholtz
 */
public class KalahTest {

    private static final Logger LOG = LoggerFactory.getLogger(KalahTest.class);

    /**
     * creates a new game and optionally creates players
     */
    private Kalah createGame(final KalahListener listener,
            boolean addPlayerOne, boolean addPlayerTwo) {
        LOG.debug("creating new game of Kalah");
        final KalahConfiguration gameConfig = new KalahConfiguration(6, 3);
        final Kalah game = new Kalah(gameConfig, listener);

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
        Kalah game = createGame(new NullListener(), false, false);
        try {
            game.getCurrentPlayer();
            fail("Should have thrown InvalidArgumentException, but did not!");
        } catch (final Exception e) {
            assertTrue("incorrect exception!", e.getMessage().startsWith("no players set!"));
        }
    }

    @Test
    public void testPlayerOne() {
        Kalah game = createGame(new NullListener(), true, false);

        assertTrue("player does not have a store!",
                game.getPlayerOne().getStore() != null);

        Assert.assertEquals("player pits initialized incorrectly",
                game.getConfiguration().getPits() * game.getConfiguration().getSeeds(),
                game.getPlayerOne().getScore(true, true));
    }

    @Test
    public void testPlayerTwo() {
        Kalah game = createGame(new NullListener(), false, true);

        assertTrue("player does not have a store!",
                game.getPlayerTwo().getStore() != null);

        Assert.assertEquals("player pits initialized incorrectly",
                game.getConfiguration().getPits() * game.getConfiguration().getSeeds(),
                game.getPlayerTwo().getScore(true, true));
    }

    @Test
    public void testOpponent() {
        Kalah game = createGame(new NullListener(), true, true);

        assertTrue("player one does not have opponent!",
                game.getPlayerOne().getOpponent() != null);
        assertTrue("player two does not have opponent!",
                game.getPlayerOne().getOpponent() != null);

        assertTrue("player one has invalid opponent!",
                game.getPlayerOne().getOpponent().equals(game.getPlayerTwo()));
        assertTrue("player two has invalid opponent!",
                game.getPlayerTwo().getOpponent().equals(game.getPlayerOne()));
    }

    /**
     * plays a move with the current player
     * @param game the game to play with
     * @param pit the pit to play from
     * @param problem if specified, indicates an exception that should occur
     * @param expectedCurrentPlayer the player that should be the current player
     * @param expectedNextPlayer  the player that should be the next player after the round has completed
     */
    private void play(Kalah game, int pit, KalahException.KalahProblem problem,
            Player expectedCurrentPlayer, Player expectedNextPlayer) {
        try {
            Assert.assertEquals("inccorrect current player!",
                    expectedCurrentPlayer, game.getCurrentPlayer());

            LOG.debug("{} is trying to sow from pit {}, the next player should be {}",
                    expectedCurrentPlayer.getPlayerName(), pit, expectedNextPlayer.getPlayerName());

            ((ArgumentPlayer) game.getCurrentPlayer()).playFromArgument(pit);
            if (problem != null) {
                fail("should have thrown exception");
            }
        } catch (KalahException ex) {
            if (problem != null) {
                LOG.debug("however he should not be able to... due to {}", ex.getProblem());
                Assert.assertEquals("incorrect problem occurred",
                        ex.getProblem(), problem);
            } else {
                fail("should not have thrown exception here! - "
                        + ex.getProblem().name());
            }
        }

        if (problem == null) {
            Assert.assertEquals("incorrect next player!", expectedNextPlayer,
                    ((TestListener)game.getKalahListener()).getNewPlayer());
        }
    }

    /**
     * checks that the pit & store scores for the player matches the specified scores
     * @param player the player to use as a source for the scores
     * @param expectedPitsAndStores a pipe delimited string that contains pits scores
     * from pit 0 up to the last pit, and then the store score, ex:
     * <pre>
     * P is for Pit
     * S is for Store
     * <tt>P|P|P|P|P|P|S</tt>
     * <tt>0|0|0|0|0|0|0</tt>
     * </pre>
     */
    private void checkPits(ArgumentPlayer player, String expectedPitsAndStores) {
        final List<String> list = Splitter.on("|").splitToList(expectedPitsAndStores);
        LOG.debug("checking store and pits for player {}, should be {}",
                player.getPlayerName(), expectedPitsAndStores);

        int pitId = 0;
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); pitId++) {
            Integer seedCountTry = Ints.tryParse(iterator.next());
            Assert.assertNotNull("expected pit count was null! test issue!", seedCountTry);

            // unbox for test
            int seedCount = seedCountTry;
            if (!iterator.hasNext() && pitId == player.getGame()
                    .getConfiguration().getPits()) {
                // this is the store (last item)
                Assert.assertEquals("player[" + player.getPlayerName()
                        + "] has incorrect ammount of seeds in his store",
                        seedCount, player.getStore().amountOfSeeds());
            } else {
                // this is a pit
                Assert.assertEquals("player[" + player.getPlayerName()
                        + "] has incorrect ammount of seeds in pit[" + pitId + "]",
                        seedCount, player.getPit(pitId).amountOfSeeds());
            }
        }
    }

    /**
     * checks that all pits for both players have the correct scores
     */
    private void checkAllPits(Kalah game, String pOnePitsAndStores, String pTwoPitsAndStores) {
        checkPits((ArgumentPlayer) game.getPlayerOne(), pOnePitsAndStores);
        checkPits((ArgumentPlayer) game.getPlayerTwo(), pTwoPitsAndStores);
    }

    /**
     * plays a full game of 6,3 kalah! :)
     */
    @Test
    public void testGame() {
        final TestListener testListener = new TestListener();
        final Kalah game = createGame(testListener, true, true);

        assertTrue("game cannot be completed, not started yet",
                game.isEndOfGame() == null);

        game.startGame();

        assertTrue("game start event should have been called",
                testListener.isGameStarted());

        // check that all seed counts are correct
        checkAllPits(game, "3|3|3|3|3|3|0", "3|3|3|3|3|3|0");

        // first test the pits
        play(game, -1, KalahException.KalahProblem.INVALID_PIT,
                game.getPlayerOne(), game.getPlayerOne());
        play(game, 6, KalahException.KalahProblem.INVALID_PIT,
                game.getPlayerOne(), game.getPlayerOne());

        // start playing
        play(game, 5, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "3|3|3|3|3|0|1", "4|4|3|3|3|3|0");

        play(game, 0, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "3|3|3|3|3|0|1", "0|5|4|4|4|3|0");

        play(game, 3, null, game.getPlayerOne(), game.getPlayerOne());
        checkAllPits(game, "3|3|3|0|4|1|2", "0|5|4|4|4|3|0");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerOne(),
                testListener.getFreeMovePlayer());

        play(game, 0, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|4|4|0|4|1|7", "0|5|0|4|4|3|0");

        play(game, 1, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "0|4|4|0|4|1|7", "0|0|1|5|5|4|1");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 2, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "0|4|4|0|4|1|7", "0|0|0|6|5|4|1");

        play(game, 2, null, game.getPlayerOne(), game.getPlayerOne());
        checkAllPits(game, "0|4|0|1|5|2|8", "0|0|0|6|5|4|1");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerOne(),
                testListener.getFreeMovePlayer());

        play(game, 1, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|0|1|2|6|3|8", "0|0|0|6|5|4|1");

        play(game, 3, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "1|1|2|2|6|3|8", "0|0|0|0|6|5|2");

        play(game, 5, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "1|1|2|2|6|0|9", "1|1|0|0|6|5|2");

        play(game, 4, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "2|2|3|3|6|0|9", "1|1|0|0|0|6|3");

        play(game, 3, null, game.getPlayerOne(), game.getPlayerOne());
        checkAllPits(game, "2|2|3|0|7|1|10", "1|1|0|0|0|6|3");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerOne(),
                testListener.getFreeMovePlayer());

        play(game, 2, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "2|2|0|1|8|2|10", "1|1|0|0|0|6|3");

        play(game, 1, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "2|2|0|0|8|2|10", "1|0|0|0|0|6|5");

        play(game, 0, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|3|1|0|8|2|10", "1|0|0|0|0|6|5");

        play(game, 5, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "1|4|2|1|9|2|10", "1|0|0|0|0|0|6");

        // check the scores quickly
        Assert.assertEquals("player 1 inccorect score!", 10,
                game.getPlayerOne().getScore(false, true));
        Assert.assertEquals("player 2 inccorect score!", 6,
                game.getPlayerTwo().getScore(false, true));

        play(game, 5, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "1|4|2|1|9|0|11", "2|0|0|0|0|0|6");

        // check that we can't click empty pits quickly
        play(game, 2, KalahException.KalahProblem.NO_SEEDS_IN_PIT,
                game.getPlayerTwo(), game.getPlayerTwo());

        play(game, 0, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "1|4|2|0|9|0|11", "0|1|0|0|0|0|8");

        play(game, 4, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "2|4|2|0|0|1|12", "1|2|1|1|1|1|8");

        play(game, 1, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "2|4|2|0|0|1|12", "1|0|2|2|1|1|8");

        play(game, 1, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "2|0|3|1|1|2|12", "1|0|2|2|1|1|8");

        play(game, 5, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "2|0|3|1|1|2|12", "1|0|2|2|1|0|9");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 3, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "0|0|3|1|1|2|12", "1|0|2|0|2|0|12");

        play(game, 2, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|2|2|3|12", "1|0|2|0|2|0|12");

        play(game, 4, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|2|2|3|12", "1|0|2|0|0|1|13");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 5, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|2|2|3|12", "1|0|2|0|0|0|14");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 2, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "0|0|0|2|2|3|12", "1|0|0|1|1|0|14");

        play(game, 3, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|0|3|4|12", "1|0|0|1|1|0|14");

        play(game, 3, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "0|0|0|0|3|4|12", "1|0|0|0|2|0|14");

        play(game, 4, null, game.getPlayerOne(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|0|0|5|13", "2|0|0|0|2|0|14");

        play(game, 4, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|0|0|5|13", "2|0|0|0|0|1|15");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 5, null, game.getPlayerTwo(), game.getPlayerTwo());
        checkAllPits(game, "0|0|0|0|0|5|13", "2|0|0|0|0|0|16");
        Assert.assertEquals("freeMove player incorrect", game.getPlayerTwo(),
                testListener.getFreeMovePlayer());

        play(game, 0, null, game.getPlayerTwo(), game.getPlayerOne());
        checkAllPits(game, "0|0|0|0|0|5|13", "0|1|1|0|0|0|16");

        // game end play!, player two should win :)
        play(game, 5, null, game.getPlayerOne(), game.getPlayerOne());
        checkAllPits(game, "0|0|0|0|0|0|14", "0|0|0|0|0|0|22");

        Assert.assertEquals("player one should have made the end game move!",
                game.isEndOfGame(), game.getPlayerOne());

        Assert.assertEquals("player two should have won!",
                testListener.getWhoWon(), game.getPlayerTwo());

        // final score check, pits should also have zero items!
        Assert.assertEquals("player 1 inccorect score!", 14,
                game.getPlayerOne().getScore(true, true));
        Assert.assertEquals("player 2 inccorect score!", 22,
                game.getPlayerTwo().getScore(true, true));

        // check game end
        assertFalse("game should be ended now",
                testListener.isGameStarted());
    }

    // --------------------------------- HELPER CLASSES -----------------------------------

    /**
     * this player can play any pit that the player owns directly from an argument
     */
    private static class ArgumentPlayer extends Player {
        public ArgumentPlayer(Kalah game, int id, String name) {
            super(game, id, name);
        }

        /**
         * expose a normally hidden method
         */
        public Pit getPit(int id) {
            return this.getPitById(id);
        }

        public void playFromArgument(int pitId) throws KalahException {
            this.sowFromPit(pitId);
        }
    }

    /**
     * this listener will ignore all game events
     */
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

    /**
     * this listener will set instance variables based on game events
     * this is to test that expect values are correct
     */
    private static class TestListener implements KalahListener {
        private final Map<SeedAcceptor, Seed> seedsAdded = Maps.newHashMap();
        private boolean gameStarted = false;
        private Player whoWon = null;
        private Player whoLost = null;
        private Player sowPlayer = null;
        private Pit sowPit = null;
        private Pit emptyPit = null;
        private Player newPlayer = null;
        private Player freeMovePlayer = null;


        @Override
        public void gameStart() {
            this.gameStarted = true;
        }

        @Override
        public void gameEnd(Player whoWon, Player whoLost) {
            this.gameStarted = false;
            this.whoWon = whoWon;
            this.whoLost = whoLost;
        }

        @Override
        public void sowStart(Player player, Pit fromPit) {
            this.getSeedsAdded().clear();
            this.freeMovePlayer = null;
            this.sowPlayer = player;
            this.sowPit = fromPit;
        }

        @Override
        public void sowEnd(Player player, Pit fromPit) {
            Assert.assertFalse("null sowEnd player!", player == null);
            Assert.assertFalse("null sowEnd pit!", fromPit == null);
            Assert.assertTrue("incorrect sowEnd player!", player.equals(this.getSowPlayer()));
            Assert.assertTrue("incorrect sowEnd pit!", fromPit.equals(this.getSowPit()));
        }

        @Override
        public void playerSwitch(Player newPlayer) {
            this.newPlayer = newPlayer;
        }

        @Override
        public void freeMove(Player forPlayer) {
            this.freeMovePlayer = forPlayer;
        }

        @Override
        public void pitEmpty(Pit pit) {
            this.emptyPit = pit;
        }

        @Override
        public void seedAdded(SeedAcceptor acceptor, Seed seed) {
            this.getSeedsAdded().put(acceptor, seed);
        }

        public Map<SeedAcceptor, Seed> getSeedsAdded() {
            return seedsAdded;
        }

        public boolean isGameStarted() {
            return gameStarted;
        }

        public Player getWhoWon() {
            return whoWon;
        }

        public Player getWhoLost() {
            return whoLost;
        }

        public Player getSowPlayer() {
            return sowPlayer;
        }

        public Pit getSowPit() {
            return sowPit;
        }

        public Pit getEmptyPit() {
            return emptyPit;
        }

        public Player getNewPlayer() {
            return newPlayer;
        }

        public Player getFreeMovePlayer() {
            return freeMovePlayer;
        }
    }
}