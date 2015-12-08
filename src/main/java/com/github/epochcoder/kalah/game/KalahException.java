package com.github.epochcoder.kalah.game;

/**
 * This game exception is used to encapsulate messages and internal
 * exceptions that should eventually end up at the user
 * @author Willie Scholtz
 */
public class KalahException extends Exception {

    private static final long serialVersionUID = 1112779592792609968L;

    /**
     * creates a new KalahException with the specified problem string
     * @param problem the problem that occurred, an invalid move, invalid parameters, etc.
     */
    public KalahException(final KalahProblem problem) {
        super(problem.name());
    }

    public static enum KalahProblem {
        INVALID_PIT,
        NO_SEEDS_IN_PIT
    }
}