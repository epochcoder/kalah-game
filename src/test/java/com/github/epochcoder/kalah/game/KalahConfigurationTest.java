package com.github.epochcoder.kalah.game;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * tests that KalahConfiguration works correctly
 * @author Willie Scholtz
 */
public class KalahConfigurationTest {

    private void testArguments(int p, int s, String m) {
        try {
            new KalahConfiguration(p, s);
            fail("Should have thrown InvalidArgumentException, but did not!");
        } catch (final IllegalArgumentException e) {
            assertTrue("incorrect exception!", e.getMessage().startsWith(m));
        }
    }

    @Test
    public void testInvalidPits1() {
        testArguments(KalahConfiguration.MIN_PIT_COUNT - 1,
                KalahConfiguration.MIN_SEEDS, "invalid amount of pits");
    }

    @Test
    public void testInvalidPits2() {
        testArguments(KalahConfiguration.MAX_PIT_COUNT + 1,
                KalahConfiguration.MIN_SEEDS, "invalid amount of pits");
    }

    @Test
    public void testInvalidSeeds1() {
        testArguments(KalahConfiguration.MIN_PIT_COUNT,
                KalahConfiguration.MIN_SEEDS - 1, "invalid amount of seeds");
    }

    @Test
    public void testInvalidSeeds2() {
        testArguments(KalahConfiguration.MIN_PIT_COUNT,
                KalahConfiguration.MAX_SEEDS + 1, "invalid amount of seeds");
    }

    @Test
    public void testValidReturn() {
        final KalahConfiguration config = new KalahConfiguration(6, 5);
        assertEquals("not correct amount of pits", 6, config.getPits());
        assertEquals("not correct amount of seeds", 5, config.getSeeds());
    }
}
