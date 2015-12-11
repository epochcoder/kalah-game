package com.github.epochcoder.kalah.servlet;

import com.github.epochcoder.kalah.game.Kalah;
import com.github.epochcoder.kalah.game.KalahConfiguration;
import com.github.epochcoder.kalah.game.KalahException;
import com.github.epochcoder.kalah.game.entity.Pit;
import com.github.epochcoder.kalah.game.entity.Player;
import com.github.epochcoder.kalah.game.entity.Seed;
import com.github.epochcoder.kalah.game.entity.SeedAcceptor;
import com.github.epochcoder.kalah.game.entity.impl.RequestPlayer;
import com.github.epochcoder.kalah.game.events.KalahListener;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extremely basic servlet that retrieves the game state
 * and allows players to make moves
 * @author Willie Scholtz
 */
@WebServlet(urlPatterns = {
    "/api"
})
public class KalahServlet extends HttpServlet {

    private static final long serialVersionUID = -4153650942420492867L;
    private static final Logger LOG = LoggerFactory.getLogger(KalahServlet.class);

    /**
     * default to a 6-seed, 6-pit game.
     * not making this configurable via client yet
     */
    private static final KalahConfiguration CONFIG = new KalahConfiguration(6, 6);

    /**
     * the session key for the kalah game
     */
    public static final String GAME_KEY = "kalah.game";

    /**
     * returns a safe string from the specified name
     * @param name a player name
     * @param name a player id
     * @return a valid player name
     */
    private static String safeName(final String name, int id) {
        return name == null || name.trim().isEmpty()
                ? "Player " + id
                : name.replaceAll("[^A-zA-Z0-9 ]", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final StringBuilder result = new StringBuilder("{\"game\":");
        PrintWriter out = null;
        try {
            out = resp.getWriter();

            // create the session if it does not exist
            final HttpSession session = req.getSession(true);
            final String action = req.getParameter("action");

            final boolean isNew = "new".equals(action);
            final boolean isPlay = "play".equals(action);

            // get the current game
            Kalah game = (Kalah) session.getAttribute(GAME_KEY);
            if (game == null && !isNew) {
                result.append("null");
            } else {
                // keep track of client exceptions
                KalahException exc = null;

                // if the new action was passed, create a new game
                if (isNew) {
                    game = new Kalah(CONFIG, new RequestGameListener());

                    final String playerOneName = safeName(req.getParameter("playerOne"), 1);
                    final String playerTwoName = safeName(req.getParameter("playerTwo"), 2);

                    final Player playerOne = new RequestPlayer(game, 1, playerOneName);
                    final Player playerTwo = new RequestPlayer(game, 2, playerTwoName);

                    game.setPlayerOne(playerOne);
                    game.setPlayerTwo(playerTwo);
                    game.startGame();
                } else if (isPlay) {
                    try {
                        // let the current player play the round
                        ((RequestPlayer) game.getCurrentPlayer()).play(req);
                    } catch (KalahException ex) {
                        exc = ex;
                    }
                }

                // always save the current game back to session
                session.setAttribute(GAME_KEY, game);

                // always return the game state to the client
                result.append(game.toString());

                // add the problem to the response
                if (exc != null) {
                    result.append(", \"problem\":").append("\"")
                            .append(exc.getProblem().toString()).append("\"");
                }

                // check if the player generated a free move
                final Player freeMovePlayer = ((RequestGameListener) game
                            .getKalahListener()).getFreeMovePlayer();
                if (freeMovePlayer != null) {
                    LOG.debug("currentPlayer player is {}", game.getCurrentPlayer());
                    LOG.debug("freeMove player is {}", freeMovePlayer);
                    result.append(", \"freeMovePlayer\":").append("\"")
                            .append(freeMovePlayer.getPlayerName()).append("\"");
                }

                // check if the game has ended
                if (game.isEndOfGame() != null) {
                    // check who won/lost
                    final Player whoWon = ((RequestGameListener) game
                            .getKalahListener()).getWinner();
                    final Player whoLost = ((RequestGameListener) game
                            .getKalahListener()).getLoser();

                    if (whoLost == null || whoWon == null) {
                        result.append(", \"tied\":true");
                    } else {
                        result.append(", \"winner\":").append("\"")
                                .append(whoWon.getPlayerName()).append("\"");
                        result.append(", \"loser\":").append("\"")
                                .append(whoLost.getPlayerName()).append("\"");
                    }
                }
            }
        } catch (IOException ex) {
            LOG.warn("error occured while processing client request", ex);
        } finally {
            resp.setContentType("application/json");
            if (out != null) {
                out.println(result.append("}").toString());
            }
        }
    }

    /**
     * only keeps track of the winner/loser
     */
    private static class RequestGameListener implements KalahListener {

        private Player winner;
        private Player loser;
        private Player freeMovePlayer;

        @Override
        public void gameStart() {}

        @Override
        public void gameEnd(Player whoWon, Player whoLost) {
            this.winner = whoWon;
            this.loser = whoLost;
        }

        public Player getWinner() {
            return this.winner;
        }

        public Player getLoser() {
            return this.loser;
        }

        public Player getFreeMovePlayer() {
            return this.freeMovePlayer;
        }

        @Override
        public void sowStart(Player player, Pit fromPit) {
            this.freeMovePlayer = null;
        }

        @Override
        public void sowEnd(Player player, Pit fromPit) {}

        @Override
        public void playerSwitch(Player newPlayer) {}

        @Override
        public void freeMove(Player forPlayer) {
            this.freeMovePlayer = forPlayer;
        }

        @Override
        public void pitEmpty(Pit pit) {}

        @Override
        public void seedAdded(SeedAcceptor acceptor, Seed seed) {}
    }
}