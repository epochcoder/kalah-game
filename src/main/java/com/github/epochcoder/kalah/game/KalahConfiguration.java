package com.github.epochcoder.kalah.game;

import com.google.common.base.Preconditions;

/**
 * This class allows the Kalah game to be configurable,
 * currently only in a sense of seeds per pit and pits per side.
 * @author Willie Scholtz
 */
public final class KalahConfiguration {

    /**
     * the maximum amount of allowed seeds per house (pit)
     */
    public static final int MAX_SEEDS = 6;

    /**
     * the minimum amount of allowed seeds per house (pit)
     */
    public static final int MIN_SEEDS = 3;

    /**
     * the total amount of houses (pits) that this game of kalah has
     */
    private static final int PIT_COUNT = 6;

    private final int pits;
    private final int seeds;

    /**
     * constructs a new instance of a <tt>KalahConfiguration</tt>
     * this constructor allows the creation of a game with
     * <tt>x</tt> pits per side, and <tt>y</tt> seeds per pit
     * @param pits the amount of pits per side of the game
     * @param seeds the amount of seeds allocated to each pit
     */
    public KalahConfiguration(final int pits, final int seeds) {
        this.pits = pits;
        this.seeds = seeds;

        // checking that we have a valid amount of pits, each player should
        // at least have one pit, but not more that the game's maximum
        Preconditions.checkArgument(this.pits >= 2 && this.pits <= PIT_COUNT,
                "invalid amount of pits, should be (%s <= pits >= 2)", PIT_COUNT);

        // ensure that the seeds per pit is valid, this is defined as a constant in Pit
        Preconditions.checkArgument(this.seeds >= MIN_SEEDS && this.seeds <= MAX_SEEDS,
                "invalid amount of seeds per pit, should be (%s <= seeds >= %s)", MAX_SEEDS, MIN_SEEDS);
    }

    /**
     * the configured amount of pits assigned to each player side
     * @return the configured amount of pits for this game
     */
    public int getPits() {
        return this.pits;
    }

    /**
     * the configured amount of seeds per pit
     * @return the configured amount of seeds for this game
     */
    public int getSeeds() {
        return this.seeds;
    }
}