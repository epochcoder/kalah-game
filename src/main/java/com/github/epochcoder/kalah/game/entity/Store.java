package com.github.epochcoder.kalah.game.entity;

import com.github.epochcoder.kalah.game.Kalah;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a store that may contain an arbitrary number of seeds,
 * this Store has no concept of where it is or to which player it belongs,
 * the <tt>Player</tt> object manages it
 * @author Willie Scholtz
 */
final class Store extends SeedAcceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Store.class);

    @Override
    public String getAcceptorId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
