package com.github.epochcoder.kalah.game.entity.impl;

import com.github.epochcoder.kalah.game.Kalah;
import com.github.epochcoder.kalah.game.KalahException;
import com.github.epochcoder.kalah.game.entity.Player;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a Player that plays from the parameters within a request object
 * @author Willie Scholtz
 */
public class RequestPlayer extends Player {

    private static final long serialVersionUID = 6871288296476778133L;
    private static final Logger LOG = LoggerFactory.getLogger(RequestPlayer.class);

    /**
     * {@inheritDoc}
     */
    public RequestPlayer(Kalah game, int playerId, String playerName) {
        super(game, playerId, playerName);
    }

    /**
     * plays a round of kalah from the specified pitId
     * @param req the request to use to retrieve the pitId
     * @throws KalahException if any play errors occur
     */
    public void play(final HttpServletRequest req) throws KalahException {
        Preconditions.checkNotNull(req, "the request was null!");
        final Integer pitId = Ints.tryParse(req.getParameter("pitId"));
        if (pitId == null) {
            LOG.debug("invalid pitId received from client!");
            throw new KalahException(KalahException.KalahProblem.INVALID_PIT);
        } else {
            this.sowFromPit(pitId);
        }
    }
}